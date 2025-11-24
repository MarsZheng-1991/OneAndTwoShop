package com.OneAndTwoShop.orderService.service;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.common.i18n.ErrorMessageService;
import com.OneAndTwoShop.commonLib.messaging.event.StockItemPayload;
import com.OneAndTwoShop.commonLib.messaging.event.StockReserveRequestEvent;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.orderService.dto.OrderCreateRequest;
import com.OneAndTwoShop.orderService.dto.OrderDetailResponse;
import com.OneAndTwoShop.orderService.dto.OrderItemResponse;
import com.OneAndTwoShop.orderService.dto.OrderSummaryResponse;
import com.OneAndTwoShop.orderService.message.publisher.OrderStockReservePublisher;
import com.OneAndTwoShop.orderService.model.Order;
import com.OneAndTwoShop.orderService.model.OrderItem;
import com.OneAndTwoShop.orderService.repository.OrderItemRepository;
import com.OneAndTwoShop.orderService.repository.OrderRepository;
import com.OneAndTwoShop.orderService.util.OrderNumberGeneratorRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ErrorMessageService errorMessageService;

    private final OrderStockReservePublisher stockPublisher;

    private final UserVerificationService userVerificationService;

    private final OrderNumberGeneratorRedis orderNumberGeneratorRedis;

    /**
     * 建立訂單（SAGA：先建 PENDING 訂單 + 發出預扣庫存事件）
     */
    public Mono<ApiData<OrderDetailResponse>> createOrder(OrderCreateRequest req, String locale) {
        return Mono.fromCallable(() -> {

                    log.info("[Order] Start create order, userId={}", req.getUserId());

                    // 0. 檢查 items
                    if (req.getItems() == null || req.getItems().isEmpty()) {
                        throw new BusinessException("order.items.empty");
                    }

                    // 1. 驗證 user 是否存在（呼叫 user-service）
                    Boolean exists = userVerificationService
                            .verifyUserExists(String.valueOf(req.getUserId()))
                            .block();
                    if (Boolean.FALSE.equals(exists)) {
                        throw new BusinessException("order.user.notfound");
                    }

                    // 2. 本地交易：建立 PENDING 訂單 + 寫入 order_items
                    Order order = createPendingOrder(req);

                    // 3. 發送「預扣庫存」事件給 product-service
                    publishStockReserveEvent(order, req);

                    // 4. 回傳 PENDING 結果
                    String msg = errorMessageService.translate("order.pending", locale);
                    OrderDetailResponse dto = mapToDetail(order);

                    return new ApiData<>(msg, dto);

                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> handleError(ex, locale));
    }

    /**
     * 建立 PENDING 訂單，保證 orders + order_items 在同一個 DB 交易內完成
     */
    @Transactional
    protected Order createPendingOrder(OrderCreateRequest req) {

        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order();
        order.setOrderNo(orderNumberGeneratorRedis.generateOrderNo());  // ⭐ 改成 Redis INCR 版本
        order.setUserId(req.getUserId());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {

            BigDecimal price = itemReq.getPrice();
            BigDecimal amount = price.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(amount);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(price);
            item.setAmount(amount);

            order.getItems().add(item);   // ⭐ 維護雙向關聯（重點）
        }

        order.setTotalAmount(total);

        log.info("[Order] PENDING order created id={}, no={}",order.getId(), order.getOrderNo());

        return orderRepository.save(order);
    }

    /**
     * 發送預扣庫存事件給 product-service（SAGA step 1 -> 2）
     */
    protected void publishStockReserveEvent(Order order, OrderCreateRequest req) {

        List<StockItemPayload> items = new ArrayList<>();
        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {
            StockItemPayload payload = new StockItemPayload();
            payload.setProductId(itemReq.getProductId());
            payload.setQuantity(itemReq.getQuantity());
            items.add(payload);
        }

        StockReserveRequestEvent event = new StockReserveRequestEvent();
        event.setOrderNo(order.getOrderNo());
        event.setUserId(order.getUserId());
        event.setItems(items);

        stockPublisher.publish(event);
    }

    // --------------------------------------------------------
    // 查詢單筆訂單
    // --------------------------------------------------------
    public Mono<ApiData<OrderDetailResponse>> getOrderById(Long id, String locale) {
        return Mono.fromCallable(() -> {

                    // 改成包含 items 的查詢！
                    Order order = orderRepository.findWithItemsById(id)
                            .orElseThrow(() -> new BusinessException("order.notfound"));

                    String msg = errorMessageService.translate("order.query.success", locale);
                    OrderDetailResponse dto = mapToDetail(order);

                    return new ApiData<>(msg, dto);

                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> handleError(ex, locale));
    }

    // --------------------------------------------------------
    // 查詢訂單列表
    // --------------------------------------------------------
    public Mono<ApiData<Page<OrderSummaryResponse>>> listOrders(Pageable pageable, String locale) {
        return Mono.fromCallable(() -> {

                    Page<Order> page = orderRepository.findAll(pageable);
                    Page<OrderSummaryResponse> mapped = page.map(this::mapToSummary);

                    String msg = errorMessageService.translate("order.list.success", locale);
                    return new ApiData<>(msg, mapped);

                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> handleError(ex, locale));
    }

    // --------------------------------------------------------
    // 刪除訂單（注意 FK 約束，要先刪 items）
    // --------------------------------------------------------
    public Mono<ApiData<Object>> deleteOrder(Long id, String locale) {
        return Mono.fromCallable(() -> {

                    Order order = orderRepository.findWithItemsById(id)
                            .orElseThrow(() -> new BusinessException("order.notfound"));

                    if (!order.getItems().isEmpty()) {
                        orderItemRepository.deleteAll(order.getItems());
                    }

                    orderRepository.delete(order);

                    String msg = errorMessageService.translate("order.deleted", locale);
                    return new ApiData<>(msg, null);

                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(ex -> handleError(ex, locale));
    }

    // --------------------------------------------------------
    // DTO Mapping
    // --------------------------------------------------------
    private OrderDetailResponse mapToDetail(Order order) {
        OrderDetailResponse dto = new OrderDetailResponse();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> list = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderItemResponse r = new OrderItemResponse();
                r.setProductId(item.getProductId());
                r.setQuantity(item.getQuantity());
                r.setPrice(item.getPrice());
                r.setAmount(item.getAmount());
                list.add(r);
            }
        }
        dto.setItems(list);
        return dto;
    }

    private OrderSummaryResponse mapToSummary(Order order) {
        OrderSummaryResponse dto = new OrderSummaryResponse();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    // --------------------------------------------------------
    // 統一錯誤處理
    // --------------------------------------------------------
    private <T> Mono<T> handleError(Throwable ex, String locale) {

        if (ex instanceof BusinessException be) {
            log.error("[Order] BusinessException key={}", be.getKey());
            return Mono.error(be);
        }

        log.error("[Order] Unknown error", ex);
        return Mono.error(new BusinessException("system.error"));
    }
}