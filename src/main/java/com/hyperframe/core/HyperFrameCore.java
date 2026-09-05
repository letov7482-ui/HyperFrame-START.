package com.hyperframe.core;

import net.minecraft.client.MinecraftClient;

public final class HyperFrameCore {

    private static HyperFrameCore instance;

    private final MinecraftClient client;
    private final PerformanceMonitor performanceMonitor;
    private final WorkloadAnalyzer workloadAnalyzer;
    private final BottleneckDetector bottleneckDetector;

    private HyperFrameCore(MinecraftClient client) {
        this.client = client;

        this.performanceMonitor = new PerformanceMonitor(client);
        this.workloadAnalyzer = new WorkloadAnalyzer(client);
        this.bottleneckDetector = new BottleneckDetector();
    }

    public static void initialize(MinecraftClient client) {
        if (instance == null) {
            instance = new HyperFrameCore(client);
        }
    }

    public static HyperFrameCore getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "HyperFrameCore has not been initialized"
            );
        }

        return instance;
    }

    public void tick() {
        performanceMonitor.tick();

        PerformanceSnapshot snapshot =
                performanceMonitor.getSnapshot();

        workloadAnalyzer.tick(snapshot);

        bottleneckDetector.analyze(
                snapshot,
                workloadAnalyzer
        );
    }

    public MinecraftClient getClient() {
        return client;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }

    public WorkloadAnalyzer getWorkloadAnalyzer() {
        return workloadAnalyzer;
    }

    public BottleneckDetector getBottleneckDetector() {
        return bottleneckDetector;
    }

    public PerformanceSnapshot getPerformanceSnapshot() {
        return performanceMonitor.getSnapshot();
    }
}
