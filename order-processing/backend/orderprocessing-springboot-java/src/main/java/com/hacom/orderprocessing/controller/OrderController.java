package com.hacom.orderprocessing.controller;

import com.hacom.orderprocessing.domain.Order;
import com.hacom.orderprocessing.repository.OrderRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/{orderId}/status")
    public Mono<ResponseEntity<String>> getOrderStatus(@PathVariable String orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(order -> ResponseEntity.ok(order.getStatus()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> getOrdersCountBetweenDates(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end) {

        return orderRepository.findByTsBetween(start, end)
                .count()
                .map(ResponseEntity::ok);
    }
}
