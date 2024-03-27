package org.example;


import com.example.grpc.CalcServiceGrpc;
import com.example.grpc.CalcServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientApp {
    private static final Logger logger = LoggerFactory.getLogger(ClientApp.class.getName());

    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Input a firstValue: ");
            int firstValue = in.nextInt();
            System.out.print("Input a lastValue: ");
            int lastValue = in.nextInt();
            in.close();

            logger.info("Client is starting...");

            AtomicInteger atomicValue = new AtomicInteger(0);
            ManagedChannel channel = ManagedChannelBuilder
                    .forTarget("localhost:8080")
                    .usePlaintext()
                    .build();
            try {
                CalcServiceGrpc.CalcServiceBlockingStub blockingStub = CalcServiceGrpc.newBlockingStub(channel);
                new Thread(() -> {
                    CalcServiceOuterClass.ValueRequest request = CalcServiceOuterClass
                            .ValueRequest
                            .newBuilder()
                            .setFirstValue(firstValue)
                            .setLastValue(lastValue)
                            .build();
                    Iterator<CalcServiceOuterClass.ValueResponse> stockQuotes = blockingStub.calc(request);
                    while (stockQuotes.hasNext()) {
                        CalcServiceOuterClass.ValueResponse stockQuote = stockQuotes.next();
                        logger.info("new value:{}", stockQuote);
                        atomicValue.set(atomicValue.get() + stockQuote.getValue());
                    }
                    logger.info("request completed");
                }).start();
                for (int i = 0; i < 50; i++) {
                    atomicValue.set(atomicValue.get() + 1);
                    logger.info("currentValue:{}", atomicValue.get());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InputMismatchException ex) {
            logger.error("You only need to enter numbers");
        } catch (Exception ex) {
            logger.error("error:{}", ex.getMessage());
        }
    }
}
