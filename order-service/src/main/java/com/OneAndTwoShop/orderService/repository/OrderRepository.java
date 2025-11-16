package com.OneAndTwoShop.orderService.repository;

import com.OneAndTwoShop.orderService.model.Order;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);
}

