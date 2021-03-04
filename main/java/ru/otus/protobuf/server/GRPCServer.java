package ru.otus.protobuf.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.service.RemoteNumberServiceImpl;

import java.io.IOException;

public class GRPCServer {
    private static final Logger logger = LoggerFactory.getLogger(GRPCServer.class);

    public static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        RemoteNumberServiceImpl remoteNumberService = new RemoteNumberServiceImpl();

        Server server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(remoteNumberService).build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("Received shutdown request");
            server.shutdown();
            logger.info("Server stopped");
        }));

        logger.info("Server waiting for client connections...");
        server.awaitTermination();
    }
}
