package com.brotherjing.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.springframework.beans.factory.annotation.Autowired;

import com.brotherjing.broadcast.Broadcast;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.BroadcastServiceGrpc;

@GrpcService
public class BroadcastServiceImpl extends BroadcastServiceGrpc.BroadcastServiceImplBase {

    @Autowired
    private Broadcast broadcast;

    @Override
    public void sendTo(BaseProto.SendRequest request, StreamObserver<BaseProto.BroadcastResponse> responseObserver) {
        broadcast.sendTo(request.getSid(), request.getCommandList());
        responseObserver.onNext(BaseProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendToAll(BaseProto.BroadcastRequest request,
            StreamObserver<BaseProto.BroadcastResponse> responseObserver) {
        broadcast.sendToAll(request.getCommandList(), request.getExcludeSelf());
        responseObserver.onNext(BaseProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
