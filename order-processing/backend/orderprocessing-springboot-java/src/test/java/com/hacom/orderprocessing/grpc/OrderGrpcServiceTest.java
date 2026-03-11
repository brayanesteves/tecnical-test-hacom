package com.hacom.orderprocessing.grpc;

import akka.actor.ActorRef;
import akka.testkit.javadsl.TestKit;
import com.hacom.orderprocessing.actor.OrderProcessorActor;
import com.hacom.orderprocessing.service.OrderActorService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderGrpcServiceTest {

    private static akka.actor.ActorSystem system;

    @BeforeAll
    public static void setup() {
        system = akka.actor.ActorSystem.create("OrderGrpcServiceTestSystem");
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testInsertOrder_TellsActor() {
        new TestKit(system) {
            {
                OrderActorService actorService = Mockito.mock(OrderActorService.class);
                when(actorService.getOrderProcessorActor()).thenReturn(getRef());

                OrderGrpcService grpcService = new OrderGrpcService(actorService);

                OrderRequest request = OrderRequest.newBuilder()
                        .setOrderId("ORD-1")
                        .setCustomerId("CUST-1")
                        .setCustomerPhoneNumber("123")
                        .addAllItems(Collections.singletonList("Item1"))
                        .build();

                @SuppressWarnings("unchecked")
                StreamObserver<OrderResponse> responseObserver = Mockito.mock(StreamObserver.class);

                grpcService.insertOrder(request, responseObserver);

                // Verify the actor received the command
                expectMsgClass(OrderProcessorActor.ProcessOrderCommand.class);
            }
        };
    }
}
