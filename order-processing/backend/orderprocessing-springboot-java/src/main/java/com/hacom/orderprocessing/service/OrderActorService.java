package com.hacom.orderprocessing.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.hacom.orderprocessing.actor.OrderProcessorActor;
import com.hacom.orderprocessing.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class OrderActorService {

    private final ActorSystem actorSystem;
    private final OrderRepository orderRepository;
    private final SmppClientService smppClientService;
    private final Counter processedOrdersCounter;

    private ActorRef orderProcessorActor;

    @Autowired
    public OrderActorService(OrderRepository orderRepository, SmppClientService smppClientService,
            Counter processedOrdersCounter) {
        this.actorSystem = ActorSystem.create("OrderProcessingSystem");
        this.orderRepository = orderRepository;
        this.smppClientService = smppClientService;
        this.processedOrdersCounter = processedOrdersCounter;
    }

    @PostConstruct
    public void init() {
        this.orderProcessorActor = actorSystem.actorOf(
                OrderProcessorActor.props(orderRepository, smppClientService, processedOrdersCounter),
                "orderProcessorActor");
    }

    public ActorRef getOrderProcessorActor() {
        return orderProcessorActor;
    }
}
