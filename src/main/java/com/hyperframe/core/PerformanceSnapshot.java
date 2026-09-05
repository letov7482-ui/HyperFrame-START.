package com.hyperframe.core;

public final class PerformanceSnapshot {

    private final int fps;
    private final double frameTime;
    private final double onePercentLow;
    private final long memoryUsed;
    private final long memoryMax;
    private final long timestamp;

    public PerformanceSnapshot(
            int fps,
            double frameTime,
            double onePercentLow,
            long memoryUsed,
            long memoryMax
    ) {
        this.fps = fps;
        this.frameTime = frameTime;
        this.onePercentLow = onePercentLow;
        this.memoryUsed = memoryUsed;
        this.memoryMax = memoryMax;
        this.timestamp = System.currentTimeMillis();
    }

    public int getFps() {
        return fps;
    }

    public double getFrameTime() {
        return frameTime;
    }

    public double getOnePercentLow() {
        return onePercentLow;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getMemoryMax() {
        return memoryMax;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getMemoryUsagePercent() {
        if (memoryMax <= 0) {
            return 0.0;
        }

        return (memoryUsed * 100.0) / memoryMax;
    }
}
