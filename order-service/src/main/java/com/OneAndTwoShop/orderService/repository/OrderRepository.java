package com.OneAndTwoShop.orderService.repository;

import com.OneAndTwoShop.orderService.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findWithItemsByOrderNo(String orderNo);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findWithItemsById(Long id);
}