package com.brotherjing.core.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SnapshotDto {
    String docId;
    String data;
    int version;
}
