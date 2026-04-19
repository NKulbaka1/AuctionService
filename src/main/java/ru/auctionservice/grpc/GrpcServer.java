package ru.auctionservice.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcServer implements SmartLifecycle {

    @Value("${grpc.server.port:9090}")
    private int port;

    private final LotGrpcServiceImpl lotGrpcService;
    private final BidGrpcServiceImpl bidGrpcService;

    private Server server;
    private volatile boolean running = false;

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(lotGrpcService)
                    .addService(bidGrpcService)
                    .addService(ProtoReflectionServiceV1.newInstance())
                    .build()
                    .start();
            running = true;
            log.info("gRPC server started on port {}", port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start gRPC server on port " + port, e);
        }
    }

    @Override
    public void stop() {
        if (server != null && !server.isShutdown()) {
            log.info("Shutting down gRPC server...");
            server.shutdown();
            try {
                if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                    server.shutdownNow();
                }
            } catch (InterruptedException e) {
                server.shutdownNow();
                Thread.currentThread().interrupt();
            }
            running = false;
            log.info("gRPC server stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
