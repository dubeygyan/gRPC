package com.github.dubeygyan.grpc.hello.server;

import com.proto.hello.HelloMessage;
import com.proto.hello.HelloRequest;
import com.proto.hello.HelloResponse;
import com.proto.hello.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        // Read the request
        HelloMessage message = request.getHello();
        String name = message.getName();
        String result = "Hello! " + name;
        //Build the response
        HelloResponse response = HelloResponse.newBuilder()
              .setResult(result)
              .build();
        // Send the response
        responseObserver.onNext(response);
        //Complete the RPC call
        responseObserver.onCompleted();
    }
}
