package com.hyperframe.core;

public final class BottleneckDetector {

    public enum Bottleneck {
        NONE,
        CPU,
        GPU,
        MEMORY,
        CHUNKS,
        ENTITIES,
        PARTICLES
    }

    private Bottleneck bottleneck = Bottleneck.NONE;
    private double confidence;

    public void analyze(
            PerformanceSnapshot performance,
            WorkloadAnalyzer workload
    ) {
        bottleneck = Bottleneck.NONE;
        confidence = 0.0;

        double frameTime = performance.getFrameTime();
        double memory = performance.getMemoryUsagePercent();

        if (memory >= 90.0) {
            bottleneck = Bottleneck.MEMORY;
            confidence = Math.min(1.0, memory / 100.0);
            return;
        }

        if (workload.getEntityCount() >= 150) {
            bottleneck = Bottleneck.ENTITIES;
            confidence = Math.min(
                    1.0,
                    workload.getEntityCount() / 300.0
            );
            return;
        }

        if (workload.getParticleCount() >= 300) {
            bottleneck = Bottleneck.PARTICLES;
            confidence = Math.min(
                    1.0,
                    workload.getParticleCount() / 600.0
            );
            return;
        }

        if (workload.getChunkLoad() >= 225) {
            bottleneck = Bottleneck.CHUNKS;
            confidence = Math.min(
                    1.0,
                    workload.getChunkLoad() / 400.0
            );
            return;
        }

        if (frameTime >= 20.0) {
            bottleneck = Bottleneck.CPU;
            confidence = Math.min(1.0, frameTime / 40.0);
            return;
        }

        if (frameTime >= 12.0) {
            bottleneck = Bottleneck.GPU;
            confidence = Math.min(1.0, frameTime / 25.0);
        }
    }

    public Bottleneck getBottleneck() {
        return bottleneck;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getBottleneckName() {
        return switch (bottleneck) {
            case NONE -> "None detected";
            case CPU -> "CPU";
            case GPU -> "GPU";
            case MEMORY -> "Memory";
            case CHUNKS -> "Chunks";
            case ENTITIES -> "Entities";
            case PARTICLES -> "Particles";
        };
    }
}
