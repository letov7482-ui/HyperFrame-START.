package com.hyperframe.adaptive;

import com.hyperframe.core.BottleneckDetector;
import com.hyperframe.core.WorkloadAnalyzer;

public final class AdaptiveDecision {

    private final String optimizationId;
    private final double score;

    private final BottleneckDetector.Bottleneck bottleneck;
    private final WorkloadAnalyzer.WorkloadType workload;

    private final String reason;

    public AdaptiveDecision(
            String optimizationId,
            double score,
            BottleneckDetector.Bottleneck bottleneck,
            WorkloadAnalyzer.WorkloadType workload,
            String reason
    ) {
        this.optimizationId = optimizationId;
        this.score = score;
        this.bottleneck = bottleneck;
        this.workload = workload;
        this.reason = reason;
    }

    public String getOptimizationId() {
        return optimizationId;
    }

    public double getScore() {
        return score;
    }

    public BottleneckDetector.Bottleneck getBottleneck() {
        return bottleneck;
    }

    public WorkloadAnalyzer.WorkloadType getWorkload() {
        return workload;
    }

    public String getReason() {
        return reason;
    }
}
