package com.brotherjing.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.brotherjing.Const;
import com.brotherjing.api.Broadcast;
import com.brotherjing.core.loadbalance.LoadBalancer;
import com.brotherjing.core.loadbalance.ServerEntity;
import com.brotherjing.proto.BaseProto;
import com.brotherjing.proto.BroadcastServiceGrpc;
import com.brotherjing.service.DiscoveryService;
import com.google.protobuf.AbstractMessageLite;

@GrpcService
public class BroadcastServiceImpl extends BroadcastServiceGrpc.BroadcastServiceImplBase {

    @Reference(interfaceClass = Broadcast.class, cluster = "directCluster")
    private Broadcast broadcast;

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private LoadBalancer loadBalancer;

    @Override
    public void sendTo(BaseProto.SendRequest request, StreamObserver<BaseProto.BroadcastResponse> responseObserver) {
        if (!CollectionUtils.isEmpty(request.getCommandList())) {
            setBroadcastIP(request.getCommand(0).getDocId());
            broadcast.sendTo(request.getSid(), request.getCommandList().stream()
                                                      .map(AbstractMessageLite::toByteArray)
                                                      .collect(Collectors.toList()));
        }
        responseObserver.onNext(BaseProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendToAll(BaseProto.BroadcastRequest request,
            StreamObserver<BaseProto.BroadcastResponse> responseObserver) {
        if (!CollectionUtils.isEmpty(request.getCommandList())) {
            setBroadcastIP(request.getCommand(0).getDocId());
            broadcast.sendToAll(request.getCommandList().stream()
                                       .map(AbstractMessageLite::toByteArray)
                                       .collect(Collectors.toList()), request.getExcludeSelf());
        }
        responseObserver.onNext(BaseProto.BroadcastResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    /**
     * Get broadcast server ip by docId, and put in RPC context.
     */
    private void setBroadcastIP(String docId) {
        ServerEntity entity = loadBalancer.select(discoveryService.getAllServers(), docId);
        RpcContext.getContext().set(Const.BROADCAST_ADDR_CONTEXT, entity);
    }
}
