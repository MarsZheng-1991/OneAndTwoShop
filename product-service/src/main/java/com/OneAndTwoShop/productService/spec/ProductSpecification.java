package com.OneAndTwoShop.productService.spec;

import com.OneAndTwoShop.productService.dto.ProductQueryRequest;
import com.OneAndTwoShop.productService.model.Product;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(ProductQueryRequest req) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (req.getName() != null && !req.getName().isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + req.getName() + "%"));
            }
            if (req.getProductCode() != null && !req.getProductCode().isEmpty()) {
                predicates.add(cb.equal(root.get("productCode"), req.getProductCode()));
            }
            if (req.getCategory() != null && !req.getCategory().isEmpty()) {
                predicates.add(cb.equal(root.get("category"), req.getCategory()));
            }
            if (req.getStatus() != null && !req.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), req.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
