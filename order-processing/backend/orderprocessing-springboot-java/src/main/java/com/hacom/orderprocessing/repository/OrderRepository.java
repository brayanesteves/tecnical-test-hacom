package com.hacom.orderprocessing.repository;

import com.hacom.orderprocessing.domain.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;

public interface OrderRepository extends ReactiveCrudRepository<Order, ObjectId> {

    Mono<Order> findByOrderId(String orderId);

    @Query("{ 'ts' : { $gte: ?0, $lte: ?1 } }")
    Flux<Order> findByTsBetween(OffsetDateTime start, OffsetDateTime end);
}
