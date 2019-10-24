package com.brotherjing.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.springframework.beans.factory.annotation.Autowired;

import com.brotherjing.broadcast.handler.WebSocketHandler;
import com.brotherjing.proto.BroadcastServiceGrpc;
import com.brotherjing.proto.TextProto;

@GrpcService
public class BroadcastServiceImpl extends BroadcastServiceGrpc.BroadcastServiceImplBase {

    @Autowired
    private WebSocketHandler broadcast;

    @Override
    public void sendTo(TextProto.SendRequest request, StreamObserver<TextProto.BroadcastResponse> responseObserver) {
        broadcast.sendTo(request.getSid(), request.getCommandList());
        responseObserver.onNext(TextProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendToAll(TextProto.BroadcastRequest request,
            StreamObserver<TextProto.BroadcastResponse> responseObserver) {
        broadcast.sendToAll(request.getCommandList(), request.getExcludeSelf());
        responseObserver.onNext(TextProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
