package com.brotherjing.core.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document
public class SnapshotDto implements Serializable {
    @Id
    String docId;
    String data;
    int version;
}
