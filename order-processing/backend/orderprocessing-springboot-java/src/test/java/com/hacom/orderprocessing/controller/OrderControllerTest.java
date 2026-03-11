package com.hacom.orderprocessing.controller;

import com.hacom.orderprocessing.domain.Order;
import com.hacom.orderprocessing.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    public void testGetOrderStatus_Success() {
        Order order = new Order("ORD-1", "CUST-1", "123", "PROCESSED", Collections.emptyList(), OffsetDateTime.now());
        Mockito.when(orderRepository.findByOrderId("ORD-1")).thenReturn(Mono.just(order));

        webTestClient.get().uri("/api/orders/ORD-1/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("PROCESSED");
    }

    @Test
    public void testGetOrderStatus_NotFound() {
        Mockito.when(orderRepository.findByOrderId("ORD-UNKNOWN")).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/orders/ORD-UNKNOWN/status")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testGetOrdersCountBetweenDates() {
        Mockito.when(orderRepository.findByTsBetween(any(), any())).thenReturn(Flux.empty());

        webTestClient.get().uri(uriBuilder -> uriBuilder
                .path("/api/orders/count")
                .queryParam("start", "2024-01-01T00:00:00Z")
                .queryParam("end", "2024-12-31T23:59:59Z")
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class).isEqualTo(0L);
    }
}
