package com.brotherjing.core.util;

public class IDUtils {

    public static String generateSnapshotPK(String docId, int version) {
        if (version < 0) {
            return docId;
        }
        return docId + "#" + version;
    }

    public static String generateSnapshotPK(String docId) {
        return generateSnapshotPK(docId, -1);
    }
}
