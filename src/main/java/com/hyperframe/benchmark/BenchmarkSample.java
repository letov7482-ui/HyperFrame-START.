package com.hyperframe.benchmark;

public final class BenchmarkSample {

    private final double averageFps;
    private final double minimumFps;
    private final double onePercentLow;
    private final double averageFrameTime;
    private final double memoryUsagePercent;
    private final long durationMs;

    public BenchmarkSample(
            double averageFps,
            double minimumFps,
            double onePercentLow,
            double averageFrameTime,
            double memoryUsagePercent,
            long durationMs
    ) {
        this.averageFps = averageFps;
        this.minimumFps = minimumFps;
        this.onePercentLow = onePercentLow;
        this.averageFrameTime = averageFrameTime;
        this.memoryUsagePercent = memoryUsagePercent;
        this.durationMs = durationMs;
    }

    public double getAverageFps() {
        return averageFps;
    }

    public double getMinimumFps() {
        return minimumFps;
    }

    public double getOnePercentLow() {
        return onePercentLow;
    }

    public double getAverageFrameTime() {
        return averageFrameTime;
    }

    public double getMemoryUsagePercent() {
        return memoryUsagePercent;
    }

    public long getDurationMs() {
        return durationMs;
    }
}
