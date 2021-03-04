package ru.otus.protobuf.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.generated.NumberResponse;

public class ClientStreamObserver implements io.grpc.stub.StreamObserver<ru.otus.protobuf.generated.NumberResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ClientStreamObserver.class);
    private long lastValue = 0;

    public synchronized long getLastValue() {
        long buffer = this.lastValue;
        this.lastValue = 0;
        return buffer;
    }

    @Override
    public void onNext(NumberResponse numberResponse) {
        long value = numberResponse.getNumber();
        logger.info("new value: {}", value);
        setLastValue(value);
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("Error: ", throwable);
    }

    @Override
    public void onCompleted() {
        logger.info("request end");
    }

    private synchronized void setLastValue(long value) {
        this.lastValue = value;
    }
}
