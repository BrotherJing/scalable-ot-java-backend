package com.brotherjing.spi;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcResult;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import com.brotherjing.Const;
import com.brotherjing.core.loadbalance.ServerEntity;

@Slf4j
public class DirectCluster implements Cluster {

    public DirectCluster() {
        super();
    }

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new ClusterInvoker<>(directory);
    }

    private static class ClusterInvoker<T> extends AbstractClusterInvoker<T> {

        ClusterInvoker(Directory<T> directory) {
            super(directory);
        }

        @Override
        protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers,
                LoadBalance loadbalance) throws RpcException {
            ServerEntity entity = (ServerEntity)RpcContext.getContext().get(Const.BROADCAST_ADDR_CONTEXT);
            checkInvokers(invokers, invocation);
            Invoker<T> targetInvoker = invokers.stream()
                                               .filter(invoker -> invoker.getUrl().getHost().equals(entity.getHost()) &&
                                                       invoker.getUrl().getPort() == entity.getDubboPort())
                                               .findFirst()
                                               .orElse(null);
            if (targetInvoker == null) {
                log.error("Invoker not found: {}", entity);
                return new RpcResult();
            }
            return targetInvoker.invoke(invocation);
        }
    }
}
