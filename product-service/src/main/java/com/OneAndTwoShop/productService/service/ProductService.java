package com.OneAndTwoShop.productService.service;

import com.OneAndTwoShop.commonLib.common.error.BusinessException;
import com.OneAndTwoShop.commonLib.common.i18n.ErrorMessageService;
import com.OneAndTwoShop.commonLib.response.ApiData;
import com.OneAndTwoShop.productService.dto.ProductQueryRequest;
import com.OneAndTwoShop.productService.model.Product;
import com.OneAndTwoShop.productService.repository.ProductRepository;
import com.OneAndTwoShop.productService.spec.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ErrorMessageService errorMessageService;

    public Mono<ApiData<Product>> create(Product product, String locale) {
        return Mono.fromCallable(() -> {
            if (repository.findByProductCode(product.getProductCode()).isPresent()) {
                throw new BusinessException("product.duplicate_code");
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
            return new ApiData<Object>(message, null);

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
}