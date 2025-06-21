package com.usp.items;

public class StatisticRecord {
    private final int chunkSize;
    private final int numPeers;
    private final long fileSize;
    private final double duration; // em segundos

    public StatisticRecord(int chunkSize, int numPeers, long fileSize, double duration) {
        this.chunkSize = chunkSize;
        this.numPeers = numPeers;
        this.fileSize = fileSize;
        this.duration = duration;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getNumPeers() {
        return numPeers;
    }

    public long getFileSize() {
        return fileSize;
    }

    public double getDuration() {
        return duration;
    }
}  

