package com.OneAndTwoShop.productService.controller;

import com.OneAndTwoShop.productService.dto.ProductResponse;
import com.OneAndTwoShop.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{code}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("code") String code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }
}