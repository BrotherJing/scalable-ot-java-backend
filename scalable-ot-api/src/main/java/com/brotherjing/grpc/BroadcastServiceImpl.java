package com.brotherjing.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

import org.apache.dubbo.config.annotation.Reference;

import com.brotherjing.api.Broadcast;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.BroadcastServiceGrpc;
import com.google.protobuf.AbstractMessageLite;

@GrpcService
public class BroadcastServiceImpl extends BroadcastServiceGrpc.BroadcastServiceImplBase {

    @Reference(interfaceClass = Broadcast.class)
    private Broadcast broadcast;

    @Override
    public void sendTo(BaseProto.SendRequest request, StreamObserver<BaseProto.BroadcastResponse> responseObserver) {
        broadcast.sendTo(request.getSid(), request.getCommandList().stream()
                                                  .map(AbstractMessageLite::toByteArray)
                                                  .collect(Collectors.toList()));
        responseObserver.onNext(BaseProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendToAll(BaseProto.BroadcastRequest request,
            StreamObserver<BaseProto.BroadcastResponse> responseObserver) {
        broadcast.sendToAll(request.getCommandList().stream()
                                   .map(AbstractMessageLite::toByteArray)
                                   .collect(Collectors.toList()), request.getExcludeSelf());
        responseObserver.onNext(BaseProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
