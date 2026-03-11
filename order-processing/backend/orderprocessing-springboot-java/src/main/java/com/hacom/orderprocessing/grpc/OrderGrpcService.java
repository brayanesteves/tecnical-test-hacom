package com.hacom.orderprocessing.grpc;

import akka.actor.ActorRef;
import com.hacom.orderprocessing.actor.OrderProcessorActor;
import com.hacom.orderprocessing.service.OrderActorService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final ActorRef orderProcessorActor;

    public OrderGrpcService(OrderActorService orderActorService) {
        this.orderProcessorActor = orderActorService.getOrderProcessorActor();
    }

    @Override
    public void insertOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        CompletableFuture<String> replyTo = new CompletableFuture<>();

        OrderProcessorActor.ProcessOrderCommand command = new OrderProcessorActor.ProcessOrderCommand(
                request.getOrderId(),
                request.getCustomerId(),
                request.getCustomerPhoneNumber(),
                request.getItemsList(),
                replyTo);

        orderProcessorActor.tell(command, ActorRef.noSender());

        replyTo.whenComplete((status, throwable) -> {
            if (throwable != null) {
                responseObserver.onError(throwable);
            } else {
                OrderResponse response = OrderResponse.newBuilder()
                        .setOrderId(request.getOrderId())
                        .setStatus(status)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        });
    }
}
