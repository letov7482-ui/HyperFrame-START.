package com.hyperframe.optimization;

public final class OptimizationResult {

    private final String optimizationId;
    private final boolean successful;

    private final double fpsBefore;
    private final double fpsAfter;

    private final double frameTimeBefore;
    private final double frameTimeAfter;

    private final double fpsChange;
    private final double fpsChangePercent;

    private final long durationMs;
    private final String message;

    public OptimizationResult(
            String optimizationId,
            boolean successful,
            double fpsBefore,
            double fpsAfter,
            double frameTimeBefore,
            double frameTimeAfter,
            long durationMs,
            String message
    ) {
        this.optimizationId = optimizationId;
        this.successful = successful;
        this.fpsBefore = fpsBefore;
        this.fpsAfter = fpsAfter;
        this.frameTimeBefore = frameTimeBefore;
        this.frameTimeAfter = frameTimeAfter;
        this.durationMs = durationMs;
        this.message = message;

        this.fpsChange = fpsAfter - fpsBefore;

        this.fpsChangePercent =
                fpsBefore > 0
                        ? ((fpsAfter - fpsBefore) / fpsBefore) * 100.0
                        : 0.0;
    }

    public String getOptimizationId() {
        return optimizationId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public double getFpsBefore() {
        return fpsBefore;
    }

    public double getFpsAfter() {
        return fpsAfter;
    }

    public double getFrameTimeBefore() {
        return frameTimeBefore;
    }

    public double getFrameTimeAfter() {
        return frameTimeAfter;
    }

    public double getFpsChange() {
        return fpsChange;
    }

    public double getFpsChangePercent() {
        return fpsChangePercent;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getMessage() {
        return message;
    }

    public boolean improvedPerformance() {
        return fpsChange > 0 || frameTimeAfter < frameTimeBefore;
    }
}
