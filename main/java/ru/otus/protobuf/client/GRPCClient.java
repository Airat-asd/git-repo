package ru.otus.protobuf.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.generated.RangeNumberRequest;
import ru.otus.protobuf.generated.RemoteNumberServiceGrpc;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GRPCClient {
    private static final Logger logger = LoggerFactory.getLogger(GRPCClient.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final long CYCLE_LIMIT = 20;
    private static final long FIRST_VALUE = 1;
    private static final long END_VALUE = 10;
    private long value = 0;

    public static void main(String[] args) {
        logger.info("Client service is starting...");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        RemoteNumberServiceGrpc.RemoteNumberServiceStub remoteNumberServiceStub = RemoteNumberServiceGrpc.newStub(channel);
        new GRPCClient().runClient(remoteNumberServiceStub);
        logger.info("Client service is over");
        channel.shutdown();
    }

    private void runClient(RemoteNumberServiceGrpc.RemoteNumberServiceStub remoteNumberServiceStub) {
        RangeNumberRequest request = RangeNumberRequest.newBuilder().setStartNumber(FIRST_VALUE).setEndNumber(END_VALUE).build();
        ClientStreamObserver clientStreamObserver = new ClientStreamObserver();
        remoteNumberServiceStub.getNumber(request, clientStreamObserver);

        var executor = Executors.newSingleThreadScheduledExecutor();
        AtomicLong atomicCounter = new AtomicLong();
        Runnable task = () -> {
            long counter = atomicCounter.getAndIncrement();
            value = value + clientStreamObserver.getLastValue() + 1;
            long currentValue = value;
            logger.info("CurrentValue: {}", currentValue);
            if (counter == CYCLE_LIMIT) {
                executor.shutdown();
            }
        };

        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }
}
