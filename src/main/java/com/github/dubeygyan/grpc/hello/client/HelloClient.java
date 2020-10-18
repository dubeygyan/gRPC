package com.github.dubeygyan.grpc.hello.client;

import com.proto.hello.HelloMessage;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HelloClient {

    public static void main(String[] args) {
        // Just a line to check if everything is correct
        System.out.println("Starting the client....");
       // Building a managed channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext() // Forced disable the SSL for development ENV
                .build();
        //Creating a sync client
        HelloServiceGrpc.HelloServiceBlockingStub helloSyncClient = HelloServiceGrpc.newBlockingStub(channel);

        //Creating an async Client
       // HelloServiceGrpc.HelloServiceFutureStub helloAsyncClient = HelloServiceGrpc.newFutureStub(channel);
        // Create The Request
        HelloMessage message = HelloMessage.newBuilder()
                .setName("Gyan")
                .build();
        HelloRequest request = HelloRequest.newBuilder()
                .setHello(message)
                .build();
        // Send The Request To Server & Collect The Response
        HelloResponse response = helloSyncClient.sayHello(request);
        // Do SomeThing With The Response
        System.out.println(response.getResult());
        System.out.println("Channel ShutDown!!!");
        channel.shutdown();
    }
}
