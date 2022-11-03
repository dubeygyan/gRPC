package com.github.dubeygyan.grpc.hello.client;

import com.github.dubeygyan.grpc.hello.client.interceptors.GenericClientInterceptor;
import com.proto.hello.HelloMessage;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HelloClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                  .usePlaintext() // Forced disable the SSL for development ENV
                  //.intercept(new GenericClientInterceptor())
                  .build();

        String input = "I am a developer";
        sayHelloUnary(channel,input);
        sayHelloInServerStream(channel,input);
        sayHelloAsClientStream(channel,input);
        sayHelloAsBiDiStream(channel,input);
        channel.shutdown();
    }



    private static void sayHelloUnary(ManagedChannel channel,String input){


        //Creating a sync client
        HelloServiceGrpc.HelloServiceBlockingStub helloSyncClient = HelloServiceGrpc.newBlockingStub(channel);

        HelloMessage message = HelloMessage.newBuilder()
                .setName(input)
                .build();
        HelloRequest request = HelloRequest.newBuilder()
                .setHello(message)
                .build();
        // Send The Request To Server & Collect The Response
        //System.out.println("Client::Sending The Request.. at :->" + new Date(System.currentTimeMillis()));
        HelloResponse response = helloSyncClient.sayHello(request);
        // Do SomeThing With The Response
        System.out.println("Client::Received The Unary Response at :->" + new Date(System.currentTimeMillis()) );
        System.out.println(response.getResult());
       // System.out.println("Channel ShutDown!!! at:->" + new Date(System.currentTimeMillis()));

    }
    private static void sayHelloInServerStream(ManagedChannel channel,String input){
        System.out.println("Started Receiving Stream Response From Server at :->" + new Date(System.currentTimeMillis()) );

        HelloServiceGrpc.HelloServiceBlockingStub helloSyncClient = HelloServiceGrpc.newBlockingStub(channel);

        HelloMessage message = HelloMessage.newBuilder()
                .setName(input)
                .build();
        HelloRequest request = HelloRequest.newBuilder()
                .setHello(message)
                .build();
        // received an iterator instead of a single response
        helloSyncClient.sayHelloAsServerStream(request).forEachRemaining(response->{
            System.out.println(response.getResult());
        });

    }
    private static void sayHelloAsClientStream(ManagedChannel channel,String input){

        CountDownLatch latch = new CountDownLatch(1);
     //create an Async Client
    HelloServiceGrpc.HelloServiceStub asyncClient = HelloServiceGrpc.newStub(channel);

    StreamObserver<HelloRequest> requestStreamObserver = asyncClient.sayHelloAsClientStream(new StreamObserver<HelloResponse>() {
        @Override
        public void onNext(HelloResponse value) {
            // response received
            System.out.println("Server Send The Single Response : --");
            System.out.println(value.getResult());
        }

        @Override
        public void onError(Throwable t) {
         // some error
        }

        @Override
        public void onCompleted() {
         // Server is done
            latch.countDown();
        }
    });
    // Sending some streamed request to server
     System.out.println("Client Started Sending Streamed Request To Server : -....");
     for(char ch : input.toCharArray()){

         System.out.println("Sending Character " + ch + " To Server ");
         requestStreamObserver.onNext(HelloRequest
                 .newBuilder()
                 .setHello(HelloMessage
                         .newBuilder()
                         .setName(Character.toString(ch))
                         .build())
                         .build());
         try {
             Thread.sleep(1000l);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
     // Sending the completed signal
     requestStreamObserver.onCompleted();
     try {
         latch.await(3L, TimeUnit.SECONDS);
     } catch (InterruptedException e) {
         e.printStackTrace();
     }

 }
    private static void sayHelloAsBiDiStream(ManagedChannel channel,String input) {
        CountDownLatch latch = new CountDownLatch(1);
        //create an Async Client
        HelloServiceGrpc.HelloServiceStub asyncClient = HelloServiceGrpc.newStub(channel);
        StreamObserver<HelloRequest> requestStreamObserver = asyncClient.sayHelloAsStream(new StreamObserver<HelloResponse>() {
            @Override
            public void onNext(HelloResponse value) {
                System.out.println(value.getResult());

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();

            }
        });
       for (char ch : input.toCharArray())
       {
           System.out.println("Sending Character " + ch + " To The Server");
           requestStreamObserver.onNext(HelloRequest
                   .newBuilder()
                   .setHello(HelloMessage
                           .newBuilder()
                           .setName(Character.toString(ch))
                           .build())
                   .build());
       }
       requestStreamObserver.onCompleted();
        try {
            latch.await(3,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
