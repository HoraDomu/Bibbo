package com.bibbo.model;

public class Edge {
    public final long sourceId;
    public final long targetId;

    public Edge(long sourceId, long targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }
}
