package com.brotherjing.core.loadbalance;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ServerEntity {
    private String host;
    private int serverPort;
    private int dubboPort;

    public String getServerAddress() {
        return host + ":" + serverPort;
    }

    public String getDubboAddress() {
        return host + ":" + dubboPort;
    }
}
