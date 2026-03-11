package com.hacom.orderprocessing.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hacom.orderprocessing.domain.Order;
import com.hacom.orderprocessing.repository.OrderRepository;
import com.hacom.orderprocessing.service.SmppClientService;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrderProcessorActor extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessorActor.class);

    private final OrderRepository orderRepository;
    private final SmppClientService smppClientService;
    private final Counter processedOrdersCounter;

    public OrderProcessorActor(OrderRepository orderRepository, SmppClientService smppClientService,
            Counter processedOrdersCounter) {
        this.orderRepository = orderRepository;
        this.smppClientService = smppClientService;
        this.processedOrdersCounter = processedOrdersCounter;
    }

    public static Props props(OrderRepository orderRepository, SmppClientService smppClientService,
            Counter processedOrdersCounter) {
        return Props.create(OrderProcessorActor.class,
                () -> new OrderProcessorActor(orderRepository, smppClientService, processedOrdersCounter));
    }

    public static class ProcessOrderCommand {
        public final String orderId;
        public final String customerId;
        public final String customerPhoneNumber;
        public final List<String> items;
        public final CompletableFuture<String> replyTo;

        public ProcessOrderCommand(String orderId, String customerId, String customerPhoneNumber, List<String> items,
                CompletableFuture<String> replyTo) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.customerPhoneNumber = customerPhoneNumber;
            this.items = items;
            this.replyTo = replyTo;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessOrderCommand.class, this::processOrder)
                .build();
    }

    private void processOrder(ProcessOrderCommand command) {
        log.info("Processing order: {}", command.orderId);

        Order order = new Order(
                command.orderId,
                command.customerId,
                command.customerPhoneNumber,
                "PROCESSED",
                command.items,
                OffsetDateTime.now());

        orderRepository.save(order).subscribe(
                savedOrder -> {
                    log.info("Order {} saved to DB successfully", savedOrder.getOrderId());

                    // Increment metric
                    processedOrdersCounter.increment();

                    // Send SMS
                    String text = "Your order " + savedOrder.getOrderId() + " has been processed";
                    smppClientService.sendSms(savedOrder.getCustomerPhoneNumber(), text);

                    // Reply to sender (Grpc service)
                    command.replyTo.complete("PROCESSED");
                },
                error -> {
                    log.error("Failed to process order {}", command.orderId, error);
                    command.replyTo.completeExceptionally(error);
                });
    }
}
