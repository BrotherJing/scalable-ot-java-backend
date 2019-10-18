package com.brotherjing.core.dto;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document
@CompoundIndex(def = "{'docId': 1, 'version': 1}")
public class CommandDto {
    @Id
    String id;

    String docId;
    int version;
    byte[] payload;
}
