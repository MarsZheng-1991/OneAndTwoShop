package com.OneAndTwoShop.productService.controller;

import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.commonLib.response.ApiResponse;
import com.OneAndTwoShop.productService.dto.DecreaseStockRequest;
import com.OneAndTwoShop.productService.dto.ProductQueryRequest;
import com.OneAndTwoShop.productService.model.Product;
import com.OneAndTwoShop.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ===============================
    // Public API (給前端用)
    // ===============================

    @PostMapping
    public ResponseEntity<ApiResponse<ApiData<Product>>> create(
            @RequestBody Product req,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Product> result = productService.create(req, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(200, result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<Product>>> update(
            @PathVariable Long id,
            @RequestBody Product req,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Product> result = productService.update(id, req, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(200, result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<Object>>> deleteProduct(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Object> result = productService.delete(id, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(200, result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApiData<Product>>> getOne(
            @PathVariable Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        ApiData<Product> result = productService.getById(id, locale).block();
        return ResponseEntity.ok(new ApiResponse<>(200, result));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<ApiData<Page<Product>>>> search(
            @RequestBody ProductQueryRequest req,
            @RequestParam int page,
            @RequestParam int size,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale) {

        Pageable pageable = PageRequest.of(page, size);
        ApiData<Page<Product>> result = productService.search(req, pageable, locale).block();

        return ResponseEntity.ok(new ApiResponse<>(200, result));
    }

    // ===============================
    // Internal API (給 Order-Service 用)
    // ===============================

    // 1) 給 Order-Service 查商品用
    @GetMapping("/internal/products/{id}")
    public ResponseEntity<Product> internalGetProduct(@PathVariable Long id) {
        Product product = productService.getByIdInternal(id);
        return ResponseEntity.ok(product);
    }

    // 2) 給 Order-Service 扣庫存用
    @PostMapping("/internal/products/{id}/decrease-stock")
    public ResponseEntity<ApiResponse<ApiData<Object>>> decreaseStock(
            @PathVariable Long id,
            @RequestBody DecreaseStockRequest req,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh") String locale
    ) {

        ApiData<Object> result = productService.decreaseStock(id, req.getQuantity(), locale).block();
        return ResponseEntity.ok(new ApiResponse<>(200, result));
    }
}