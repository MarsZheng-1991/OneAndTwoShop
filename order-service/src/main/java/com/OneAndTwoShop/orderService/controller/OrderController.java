package com.OneAndTwoShop.orderService.controller;

import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.commonLib.response.ApiResponse;
import com.OneAndTwoShop.orderService.dto.OrderCreateRequest;
import com.OneAndTwoShop.orderService.dto.OrderDetailResponse;
import com.OneAndTwoShop.orderService.dto.OrderSummaryResponse;
import com.OneAndTwoShop.orderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 建立訂單（支援單項 / 多項商品）
    @PostMapping
    public ResponseEntity<ApiResponse<ApiData<OrderDetailResponse>>> createOrder(
            @RequestBody OrderCreateRequest req,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<OrderDetailResponse> result = orderService.createOrder(req, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }

    // 查詢單筆訂單（含明細）
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<OrderDetailResponse>>> getOrder(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<OrderDetailResponse> result = orderService.getOrderById(id, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }

    // 查詢訂單列表（分頁）
    @GetMapping
    public ResponseEntity<ApiResponse<ApiData<Page<OrderSummaryResponse>>>> listOrders(
            @RequestParam int page,
            @RequestParam int size,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        Pageable pageable = PageRequest.of(page, size);
        ApiData<Page<OrderSummaryResponse>> result = orderService.listOrders(pageable, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }

    // 刪除 / 取消訂單
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<Object>>> deleteOrder(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Object> result = orderService.deleteOrder(id, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), result));
    }
}