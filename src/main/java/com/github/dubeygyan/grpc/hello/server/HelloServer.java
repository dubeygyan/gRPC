package com.github.dubeygyan.grpc.hello.server;

import com.github.dubeygyan.grpc.hello.server.interceptors.GenericServerInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class HelloServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Just a line to chek everything is correct
       // System.out.println("Hello gRPC");

        Server server = ServerBuilder.forPort(50051)
                .addService(new HelloServiceImpl())
               // .intercept(new GenericServerInterceptor())
                .build();
        // Starting The Server..
       // System.out.println("Server::Starting The Server On Port " + server.getPort());
        server.start();
       // System.out.println("Server::Started The Server On Port " + server.getPort());
        // To keep the server up and running adding a shutdown hook
        // otherwise the server will start and finish immediately
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Server shutdown requested.....");
            server.shutdown();
            System.out.println("Server shutdown successfully!");
        }));
        // Wait For the actual termination request
        server.awaitTermination();
    }
}
