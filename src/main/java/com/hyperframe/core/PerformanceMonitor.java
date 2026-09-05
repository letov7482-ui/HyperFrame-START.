package com.hyperframe.core;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;

public final class PerformanceMonitor {

    private static final int HISTORY_SIZE = 300;

    private final MinecraftClient client;
    private final Deque<Double> frameTimes = new ArrayDeque<>();
    private final Deque<Integer> fpsHistory = new ArrayDeque<>();

    private PerformanceSnapshot currentSnapshot;

    private long lastUpdate;
    private double measuredFrameTime;
    private int measuredFps;

    public PerformanceMonitor(MinecraftClient client) {
        this.client = client;
        this.lastUpdate = System.nanoTime();
    }

    public void tick() {
        long now = System.nanoTime();

        if (lastUpdate == 0) {
            lastUpdate = now;
            return;
        }

        double deltaMs = (now - lastUpdate) / 1_000_000.0;
        lastUpdate = now;

        if (deltaMs <= 0 || deltaMs > 1000) {
            return;
        }

        measuredFrameTime = deltaMs;
        measuredFps = (int) Math.round(1000.0 / deltaMs);

        int minecraftFps = client.getCurrentFps();

        if (minecraftFps > 0) {
            measuredFps = minecraftFps;
        }

        addFrameTime(deltaMs);
        addFps(measuredFps);

        currentSnapshot = createSnapshot();
    }

    private void addFrameTime(double value) {
        frameTimes.addLast(value);

        while (frameTimes.size() > HISTORY_SIZE) {
            frameTimes.removeFirst();
        }
    }

    private void addFps(int value) {
        fpsHistory.addLast(value);

        while (fpsHistory.size() > HISTORY_SIZE) {
            fpsHistory.removeFirst();
        }
    }

    private PerformanceSnapshot createSnapshot() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();

        return new PerformanceSnapshot(
                measuredFps,
                measuredFrameTime,
                calculateOnePercentLow(),
                usedMemory,
                maxMemory
        );
    }

    private double calculateOnePercentLow() {
        if (fpsHistory.isEmpty()) {
            return measuredFps;
        }

        int[] values = new int[fpsHistory.size()];
        int index = 0;

        for (int fps : fpsHistory) {
            values[index++] = fps;
        }

        java.util.Arrays.sort(values);

        int count = Math.max(1, (int) Math.ceil(values.length * 0.01));

        long total = 0;

        for (int i = 0; i < count; i++) {
            total += values[i];
        }

        return total / (double) count;
    }

    public PerformanceSnapshot getSnapshot() {
        if (currentSnapshot == null) {
            return new PerformanceSnapshot(
                    client.getCurrentFps(),
                    0.0,
                    client.getCurrentFps(),
                    0,
                    Runtime.getRuntime().maxMemory()
            );
        }

        return currentSnapshot;
    }

    public int getFps() {
        return getSnapshot().getFps();
    }

    public double getFrameTime() {
        return getSnapshot().getFrameTime();
    }

    public double getOnePercentLow() {
        return getSnapshot().getOnePercentLow();
    }

    public long getMemoryUsed() {
        return getSnapshot().getMemoryUsed();
    }

    public long getMemoryMax() {
        return getSnapshot().getMemoryMax();
    }

    public double getMemoryUsagePercent() {
        return getSnapshot().getMemoryUsagePercent();
    }

    public double getAverageFps() {
        if (fpsHistory.isEmpty()) {
            return getFps();
        }

        long total = 0;

        for (int fps : fpsHistory) {
            total += fps;
        }

        return total / (double) fpsHistory.size();
    }

    public int getMinimumFps() {
        if (fpsHistory.isEmpty()) {
            return getFps();
        }

        int minimum = Integer.MAX_VALUE;

        for (int fps : fpsHistory) {
            minimum = Math.min(minimum, fps);
        }

        return minimum;
    }

    public boolean hasEnoughData() {
        return fpsHistory.size() >= 30;
    }

    public void reset() {
        frameTimes.clear();
        fpsHistory.clear();

        measuredFrameTime = 0;
        measuredFps = 0;
        currentSnapshot = null;
        lastUpdate = System.nanoTime();
    }
    }
