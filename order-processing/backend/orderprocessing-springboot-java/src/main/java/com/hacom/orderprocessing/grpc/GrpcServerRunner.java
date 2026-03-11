package com.hacom.orderprocessing.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class GrpcServerRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerRunner.class);

    private final OrderGrpcService orderGrpcService;
    private Server server;

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    public GrpcServerRunner(OrderGrpcService orderGrpcService) {
        this.orderGrpcService = orderGrpcService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting gRPC Server on port {}", grpcPort);
        server = ServerBuilder.forPort(grpcPort)
                .addService(orderGrpcService)
                .build()
                .start();

        log.info("gRPC Server started, listening on {}", grpcPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown
            // hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            GrpcServerRunner.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            log.info("Shutting down gRPC server");
            server.shutdown();
        }
    }
}
