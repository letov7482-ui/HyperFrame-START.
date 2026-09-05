package com.hyperframe.core;

import net.minecraft.client.MinecraftClient;

public final class WorkloadAnalyzer {

    public enum WorkloadType {
        IDLE,
        LIGHT,
        MODERATE,
        HEAVY,
        EXTREME
    }

    private final MinecraftClient client;
    private WorkloadType workloadType = WorkloadType.IDLE;

    private double score;
    private int entityCount;
    private int particleCount;
    private int chunkLoad;

    public WorkloadAnalyzer(MinecraftClient client) {
        this.client = client;
    }

    public void tick(PerformanceSnapshot snapshot) {
        if (client.world == null || client.player == null) {
            workloadType = WorkloadType.IDLE;
            score = 0;
            entityCount = 0;
            particleCount = 0;
            chunkLoad = 0;
            return;
        }

        entityCount = client.world.getEntityLookup().size();

        particleCount = client.particleManager != null
                ? client.particleManager.getDebugString().length()
                : 0;

        int simulationDistance = client.options.getSimulationDistance().getValue();
        chunkLoad = simulationDistance * simulationDistance;

        double entityScore = Math.min(entityCount / 150.0, 1.0);
        double particleScore = Math.min(particleCount / 500.0, 1.0);
        double chunkScore = Math.min(chunkLoad / 256.0, 1.0);

        double frameScore = 0;

        if (snapshot.getFrameTime() > 0) {
            frameScore = Math.min(snapshot.getFrameTime() / 33.3, 1.0);
        }

        score =
                entityScore * 0.30 +
                particleScore * 0.10 +
                chunkScore * 0.20 +
                frameScore * 0.40;

        workloadType = determineWorkload(score);
    }

    private WorkloadType determineWorkload(double value) {
        if (value < 0.15) {
            return WorkloadType.IDLE;
        }

        if (value < 0.35) {
            return WorkloadType.LIGHT;
        }

        if (value < 0.60) {
            return WorkloadType.MODERATE;
        }

        if (value < 0.82) {
            return WorkloadType.HEAVY;
        }

        return WorkloadType.EXTREME;
    }

    public WorkloadType getWorkloadType() {
        return workloadType;
    }

    public double getScore() {
        return score;
    }

    public int getEntityCount() {
        return entityCount;
    }

    public int getParticleCount() {
        return particleCount;
    }

    public int getChunkLoad() {
        return chunkLoad;
    }

    public String getWorkloadName() {
        return switch (workloadType) {
            case IDLE -> "Idle";
            case LIGHT -> "Light";
            case MODERATE -> "Moderate";
            case HEAVY -> "Heavy";
            case EXTREME -> "Extreme";
        };
    }
}
