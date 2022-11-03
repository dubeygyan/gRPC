package com.github.dubeygyan.grpc.hello.server;

import com.proto.hello.*;
import io.grpc.stub.StreamObserver;

import java.util.Date;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        // Read the request
        System.out.println("Server::Received The Request at :->" + new Date(System.currentTimeMillis()));
        HelloMessage message = request.getHello();
        String name = message.getName();
        String result = "Hello! " + name;
        //Build the response
        HelloResponse response = HelloResponse.newBuilder()
                .setResult(result)
                .build();
        // Send the response
        System.out.println("Server::Sending The Response at :->" + new Date(System.currentTimeMillis()));
        responseObserver.onNext(response);
        //Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloAsServerStream(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        {
            String name = request.getHello().getName();
            try {
                for (char ch : name.toCharArray()) {
                    String result = "Hello For The Character : " + ch + " : In " + name + " From Server!";
                    HelloResponse response = HelloResponse.newBuilder()
                            .setResult(result)
                            .build();
                    responseObserver.onNext(response);
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                responseObserver.onCompleted();
            }
        }
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloAsClientStream(StreamObserver<HelloResponse> responseObserver) {
        StreamObserver<HelloRequest> requestStreamObserver = new StreamObserver<HelloRequest>() {
           String result = "";
            @Override
            public void onNext(HelloRequest value) {
                // Clients sends a request
                result = result + value.getHello().getName();
            }

            @Override
            public void onError(Throwable t) {
             // Some Error occurred
            }

            @Override
            public void onCompleted() {
                // Client Done with the request
                responseObserver.onNext(
                        HelloResponse
                                .newBuilder()
                                .setResult("Hello! " +result)
                                .build());
                responseObserver.onCompleted();

            }
        };
        return requestStreamObserver;
    }

    @Override
    public StreamObserver<HelloRequest> sayHelloAsStream(StreamObserver<HelloResponse> responseObserver) {
        StreamObserver<HelloRequest> requestStreamObserver = new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest value) {
                HelloResponse response = HelloResponse
                        .newBuilder()
                        .setResult("Hello From Server : -- " +value.getHello().getName())
                        .build();
                responseObserver.onNext(response);

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();

            }
        };
        return requestStreamObserver;
    }
}
