package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.generated.RangeNumberRequest;
import ru.otus.protobuf.generated.NumberResponse;
import ru.otus.protobuf.generated.RemoteNumberServiceGrpc;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RemoteNumberServiceImpl extends RemoteNumberServiceGrpc.RemoteNumberServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(RemoteNumberServiceImpl.class);

    @Override
    public void getNumber(RangeNumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        var currentValue = new AtomicLong(request.getStartNumber());
        var executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            long value = currentValue.incrementAndGet();
            NumberResponse response = NumberResponse.newBuilder().setNumber(value).build();
            responseObserver.onNext(response);
            if (value == request.getEndNumber()) {
                executor.shutdown();
                responseObserver.onCompleted();
                logger.info("Response of server finished");
            }
        };
        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }
}
