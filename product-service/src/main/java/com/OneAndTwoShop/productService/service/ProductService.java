package com.OneAndTwoShop.productService.service;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.common.i18n.ErrorMessageService;
import com.OneAndTwoShop.commonLib.messaging.event.StockItemPayload;
import com.OneAndTwoShop.commonLib.messaging.event.StockReserveRequestEvent;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.productService.dto.ProductQueryRequest;
import com.OneAndTwoShop.productService.model.Product;
import com.OneAndTwoShop.productService.repository.ProductRepository;
import com.OneAndTwoShop.productService.spec.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repository;
    private final ErrorMessageService errorMessageService;

    public Mono<ApiData<Product>> create(Product product, String locale) {
        return Mono.fromCallable(() -> {
            if (repository.findByProductCode(product.getProductCode()).isPresent()) {
                throw new BusinessException("product.duplicate");
            }

            Product saved = repository.save(product);
            String message = errorMessageService.translate("product.created", locale);
            return new ApiData<>(message, saved);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ApiData<Product>> update(Long id, Product req, String locale) {
        return Mono.fromCallable(() -> {
            Product db = repository.findById(id)
                    .orElseThrow(() -> new BusinessException("product.notfound"));

            db.setName(req.getName());
            db.setPrice(req.getPrice());
            db.setStock(req.getStock());
            db.setCategory(req.getCategory());
            db.setStatus(req.getStatus());

            Product updated = repository.save(db);
            String message = errorMessageService.translate("product.updated", locale);
            return new ApiData<>(message, updated);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ApiData<Object>> delete(Long id, String locale) {
        return Mono.fromCallable(() -> {

            Product db = repository.findById(id)
                    .orElseThrow(() -> new BusinessException("product.notfound"));

            repository.delete(db);

            String message = errorMessageService.translate("product.deleted", locale);
            return new ApiData<>(message, null);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ApiData<Product>> getById(Long id, String locale) {
        return Mono.fromCallable(() -> {

            Product db = repository.findById(id)
                    .orElseThrow(() -> new BusinessException("product.notfound"));

            String message = errorMessageService.translate("product.query.success", locale);
            return new ApiData<>(message, db);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ApiData<Page<Product>>> search(ProductQueryRequest req, Pageable pageable, String locale) {
        return Mono.fromCallable(() -> {
            Page<Product> page = repository.findAll(ProductSpecification.filter(req), pageable);
            String message = errorMessageService.translate("product.list.success", locale);
            return new ApiData<>(message, page);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Internal API - 提供給 order-service
    public Product getByIdInternal(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("product.notfound"));
    }

    public Mono<ApiData<Object>> decreaseStock(Long productId, Integer qty, String locale) {
        return Mono.fromCallable(() -> {

            Product product = repository.findById(productId)
                    .orElseThrow(() -> new BusinessException("product.notfound"));

            if (product.getStock() < qty) {
                throw new BusinessException("product.stock.notenough");
            }

            try {
                product.setStock(product.getStock() - qty);
                repository.save(product);
            } catch (Exception e) {
                log.error("[Product] 扣庫存失敗: {}", e.getMessage(), e);
                throw new BusinessException("product.stock.decrease.fail");
            }

            String msg = errorMessageService.translate("product.stock.decrease.success", locale);
            return new ApiData<>(msg, null);

        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Saga Step：為某張訂單預扣庫存（全部商品都要成功才真正扣）
     */
    @Transactional
    public void reserveStockForOrder(StockReserveRequestEvent event) {

        log.info("[ProductService] reserveStockForOrder orderNo={}", event.getOrderNo());

        // 1. 先檢查所有商品庫存是否足夠
        for (StockItemPayload item : event.getItems()) {

            Product product = repository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("product.notfound"));

            if (product.getStock() < item.getQuantity()) {
                log.error("[ProductService] Not enough stock productId={}, stock={}, need={}",
                        product.getId(), product.getStock(), item.getQuantity());
                throw new BusinessException("product.stock.notenough");
            }
        }

        // 2. 全部都足夠才實際扣庫存
        for (StockItemPayload item : event.getItems()) {

            Product product = repository.findById(item.getProductId())
                    .orElseThrow(() -> new BusinessException("product.notfound"));

            product.setStock(product.getStock() - item.getQuantity());
            repository.save(product);
        }

        log.info("[ProductService] reserveStockForOrder OK orderNo={}", event.getOrderNo());
    }
}