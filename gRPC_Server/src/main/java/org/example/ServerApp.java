package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.service.GreetingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Hello world!
 */
public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class.getName());
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new GreetingServiceImpl())
                .build();

        server.start();

        logger.info("Server started");

        server.awaitTermination();
    }
}
