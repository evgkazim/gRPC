package org.example.service;

import com.example.grpc.CalcServiceGrpc;
import com.example.grpc.CalcServiceOuterClass;

import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends CalcServiceGrpc.CalcServiceImplBase {
    @Override
    public void calc(CalcServiceOuterClass.ValueRequest request,
                         StreamObserver<CalcServiceOuterClass.ValueResponse> responseObserver) {

        for (int i = request.getFirstValue(); i < request.getLastValue() + 1; i++) {
            CalcServiceOuterClass.ValueResponse response = CalcServiceOuterClass
                    .ValueResponse
                    .newBuilder()
                    .setValue(i)
                    .build();
            responseObserver.onNext(response);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        responseObserver.onCompleted();
    }
}
