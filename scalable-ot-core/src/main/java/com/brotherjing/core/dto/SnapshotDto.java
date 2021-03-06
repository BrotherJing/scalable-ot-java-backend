package com.brotherjing.core.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document
@CompoundIndex(def = "{'docId': 1, 'version': 1}")
public class SnapshotDto implements Serializable, Cloneable {
    @Id
    String id;

    String docId;
    String data;
    int version;

    @Override
    public SnapshotDto clone() {
        return new SnapshotDtoBuilder()
                .id(id)
                .docId(docId)
                .data(data)
                .version(version)
                .build();
    }
}
