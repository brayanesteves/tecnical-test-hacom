package com.hacom.orderprocessing.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.hacom.orderprocessing.domain.Order;
import com.hacom.orderprocessing.repository.OrderRepository;
import com.hacom.orderprocessing.service.SmppClientService;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class OrderProcessorActorTest {

    private static ActorSystem system;

    @BeforeAll
    public static void setup() {
        system = ActorSystem.create("OrderProcessorActorTestSystem");
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testProcessOrder() throws Exception {
        new TestKit(system) {
            {
                OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
                SmppClientService smppClientService = Mockito.mock(SmppClientService.class);
                Counter counter = Mockito.mock(Counter.class);

                Order savedOrder = new Order("ORD-1", "CUST-1", "123", "PROCESSED", Collections.emptyList(), null);
                Mockito.when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(savedOrder));

                final ActorRef subject = system
                        .actorOf(OrderProcessorActor.props(orderRepository, smppClientService, counter));

                CompletableFuture<String> replyTo = new CompletableFuture<>();
                OrderProcessorActor.ProcessOrderCommand command = new OrderProcessorActor.ProcessOrderCommand(
                        "ORD-1", "CUST-1", "123", Collections.emptyList(), replyTo);

                subject.tell(command, getRef());

                String result = replyTo.get(5, TimeUnit.SECONDS);
                assertEquals("PROCESSED", result);

                Mockito.verify(orderRepository).save(any(Order.class));
                Mockito.verify(counter).increment();
                Mockito.verify(smppClientService).sendSms(any(), any());
            }
        };
    }
}
