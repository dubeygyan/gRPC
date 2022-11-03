package com.github.dubeygyan.grpc.hello.client.interceptors;

import io.grpc.*;

public class GenericClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return null;
    }
}
