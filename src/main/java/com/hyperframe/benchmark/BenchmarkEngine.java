package com.hyperframe.benchmark;

import com.hyperframe.core.HyperFrameCore;
import com.hyperframe.core.PerformanceMonitor;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;

public final class BenchmarkEngine {

    private static final int MINIMUM_SAMPLES = 60;
    private static final int MAX_SAMPLES = 1200;

    private static final long DEFAULT_WARMUP_MS = 3000;
    private static final long DEFAULT_TEST_DURATION_MS = 10000;

    private static BenchmarkEngine instance;

    private final MinecraftClient client;
    private final HyperFrameCore core;
    private final PerformanceMonitor monitor;

    private final Deque<Double> fpsSamples =
            new ArrayDeque<>();

    private final Deque<Double> frameTimeSamples =
            new ArrayDeque<>();

    private BenchmarkState state =
            BenchmarkState.IDLE;

    private long warmupDurationMs =
            DEFAULT_WARMUP_MS;

    private long testDurationMs =
            DEFAULT_TEST_DURATION_MS;

    private long stateStartedAt;
    private long benchmarkStartedAt;

    private BenchmarkSample lastResult;

    private boolean collecting;

    private BenchmarkEngine(
            MinecraftClient client,
            HyperFrameCore core
    ) {
        this.client = client;
        this.core = core;
        this.monitor =
                core.getPerformanceMonitor();
    }

    public static void initialize(
            MinecraftClient client,
            HyperFrameCore core
    ) {
        if (instance != null) {
            return;
        }

        instance = new BenchmarkEngine(
                client,
                core
        );
    }

    public static BenchmarkEngine getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "BenchmarkEngine has not been initialized"
            );
        }

        return instance;
    }

    public void tick() {

        if (state == BenchmarkState.IDLE
                || state == BenchmarkState.FINISHED
                || state == BenchmarkState.CANCELLED
                || state == BenchmarkState.INSUFFICIENT_DATA) {
            return;
        }

        long now =
                System.currentTimeMillis();

        if (state == BenchmarkState.WARMUP) {

            if (now - stateStartedAt
                    >= warmupDurationMs) {

                beginMeasurement(now);
            }

            return;
        }

        if (state == BenchmarkState.RUNNING) {

            collectSample();

            if (now - benchmarkStartedAt
                    >= testDurationMs) {

                finishBenchmark(now);
            }
        }
    }

    public void start() {

        if (state == BenchmarkState.WARMUP
                || state == BenchmarkState.RUNNING) {
            return;
        }

        clearSamples();

        lastResult = null;

        state =
                BenchmarkState.WARMUP;

        stateStartedAt =
                System.currentTimeMillis();

        benchmarkStartedAt = 0;

        collecting = false;
    }

    public void cancel() {

        if (state != BenchmarkState.WARMUP
                && state != BenchmarkState.RUNNING) {
            return;
        }

        collecting = false;

        state =
                BenchmarkState.CANCELLED;
    }

    private void beginMeasurement(long now) {

        clearSamples();

        benchmarkStartedAt = now;

        collecting = true;

        state =
                BenchmarkState.RUNNING;
    }

    private void collectSample() {

        if (!collecting) {
            return;
        }

        double fps =
                monitor.getFps();

        double frameTime =
                monitor.getFrameTime();

        if (fps <= 0 || frameTime <= 0) {
            return;
        }

        addSample(
                fpsSamples,
                fps
        );

        addSample(
                frameTimeSamples,
                frameTime
        );
    }

    private void addSample(
            Deque<Double> samples,
            double value
    ) {

        samples.addLast(value);

        while (samples.size()
                > MAX_SAMPLES) {

            samples.removeFirst();
        }
    }

    private void finishBenchmark(long now) {

        collecting = false;

        if (fpsSamples.size()
                < MINIMUM_SAMPLES) {

            state =
                    BenchmarkState.INSUFFICIENT_DATA;

            lastResult = null;

            return;
        }

        lastResult =
                createResult(
                        now - benchmarkStartedAt
                );

        state =
                BenchmarkState.FINISHED;
    }

    private BenchmarkSample createResult(
            long durationMs
    ) {

        double averageFps =
                calculateAverage(
                        fpsSamples
                );

        double minimumFps =
                calculateMinimum(
                        fpsSamples
                );

        double onePercentLow =
                calculateOnePercentLow(
                        fpsSamples
                );

        double averageFrameTime =
                calculateAverage(
                        frameTimeSamples
                );

        double memoryUsage =
                monitor.getMemoryUsagePercent();

        return new BenchmarkSample(
                averageFps,
                minimumFps,
                onePercentLow,
                averageFrameTime,
                memoryUsage,
                durationMs
        );
    }

    private double calculateAverage(
            Deque<Double> samples
    ) {

        if (samples.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;

        for (double value : samples) {
            total += value;
        }

        return total / samples.size();
    }

    private double calculateMinimum(
            Deque<Double> samples
    ) {

        if (samples.isEmpty()) {
            return 0.0;
        }

        double minimum =
                Double.MAX_VALUE;

        for (double value : samples) {
            minimum =
                    Math.min(
                            minimum,
                            value
                    );
        }

        return minimum;
    }

    private double calculateOnePercentLow(
            Deque<Double> samples
    ) {

        if (samples.isEmpty()) {
            return 0.0;
        }

        ArrayList<Double> sorted =
                new ArrayList<>(samples);

        sorted.sort(Double::compareTo);

        int count =
                Math.max(
                        1,
                        (int) Math.ceil(
                                sorted.size() * 0.01
                        )
                );

        double total = 0.0;

        for (int i = 0; i < count; i++) {
            total += sorted.get(i);
        }

        return total / count;
    }

    public BenchmarkComparison compare(
            BenchmarkSample before,
            BenchmarkSample after
    ) {

        if (before == null
                || after == null) {

            return BenchmarkComparison.invalid();
        }

        return new BenchmarkComparison(
                before,
                after
        );
    }

    private void clearSamples() {

        fpsSamples.clear();
        frameTimeSamples.clear();
    }

    public BenchmarkState getState() {
        return state;
    }

    public BenchmarkSample getLastResult() {
        return lastResult;
    }

    public boolean isRunning() {
        return state == BenchmarkState.WARMUP
                || state == BenchmarkState.RUNNING;
    }

    public boolean isCollecting() {
        return collecting;
    }

    public int getSampleCount() {
        return fpsSamples.size();
    }

    public long getElapsedTimeMs() {

        if (state == BenchmarkState.RUNNING) {

            return System.currentTimeMillis()
                    - benchmarkStartedAt;
        }

        if (state == BenchmarkState.FINISHED
                && lastResult != null) {

            return lastResult.getDurationMs();
        }

        return 0;
    }

    public long getRemainingTimeMs() {

        if (state != BenchmarkState.RUNNING) {
            return 0;
        }

        return Math.max(
                0,
                testDurationMs
                        - getElapsedTimeMs()
        );
    }

    public double getProgress() {

        if (state == BenchmarkState.WARMUP) {

            long elapsed =
                    System.currentTimeMillis()
                            - stateStartedAt;

            return Math.min(
                    1.0,
                    elapsed
                            / (double) warmupDurationMs
            );
        }

        if (state == BenchmarkState.RUNNING) {

            return Math.min(
                    1.0,
                    getElapsedTimeMs()
                            / (double) testDurationMs
            );
        }

        if (state == BenchmarkState.FINISHED) {
            return 1.0;
        }

        return 0.0;
    }

    public void setWarmupDurationMs(
            long durationMs
    ) {

        warmupDurationMs =
                Math.max(
                        500,
                        durationMs
                );
    }

    public void setTestDurationMs(
            long durationMs
    ) {

        testDurationMs =
                Math.max(
                        1000,
                        durationMs
                );
    }

    public long getWarmupDurationMs() {
        return warmupDurationMs;
    }

    public long getTestDurationMs() {
        return testDurationMs;
    }

    public MinecraftClient getClient() {
        return client;
    }

    public HyperFrameCore getCore() {
        return core;
    }

    public static final class BenchmarkComparison {

        private final boolean valid;

        private final double fpsBefore;
        private final double fpsAfter;

        private final double onePercentLowBefore;
        private final double onePercentLowAfter;

        private final double frameTimeBefore;
        private final double frameTimeAfter;

        private final double fpsChange;
        private final double fpsChangePercent;

        private final double onePercentLowChange;
        private final double onePercentLowChangePercent;

        private final double frameTimeChange;
        private final double frameTimeChangePercent;

        private BenchmarkComparison(
                BenchmarkSample before,
                BenchmarkSample after
        ) {
            this.valid = true;

            this.fpsBefore =
                    before.getAverageFps();

            this.fpsAfter =
                    after.getAverageFps();

            this.onePercentLowBefore =
                    before.getOnePercentLow();

            this.onePercentLowAfter =
                    after.getOnePercentLow();

            this.frameTimeBefore =
                    before.getAverageFrameTime();

            this.frameTimeAfter =
                    after.getAverageFrameTime();

            this.fpsChange =
                    fpsAfter - fpsBefore;

            this.fpsChangePercent =
                    fpsBefore > 0
                            ? (fpsChange
                            / fpsBefore) * 100.0
                            : 0.0;

            this.onePercentLowChange =
                    onePercentLowAfter
                            - onePercentLowBefore;

            this.onePercentLowChangePercent =
                    onePercentLowBefore > 0
                            ? (onePercentLowChange
                            / onePercentLowBefore)
                            * 100.0
                            : 0.0;

            this.frameTimeChange =
                    frameTimeAfter
                            - frameTimeBefore;

            this.frameTimeChangePercent =
                    frameTimeBefore > 0
                            ? (frameTimeChange
                            / frameTimeBefore)
                            * 100.0
                            : 0.0;
        }

        private BenchmarkComparison() {
            this.valid = false;

            fpsBefore = 0;
            fpsAfter = 0;

            onePercentLowBefore = 0;
            onePercentLowAfter = 0;

            frameTimeBefore = 0;
            frameTimeAfter = 0;

            fpsChange = 0;
            fpsChangePercent = 0;

            onePercentLowChange = 0;
            onePercentLowChangePercent = 0;

            frameTimeChange = 0;
            frameTimeChangePercent = 0;
        }

        public static BenchmarkComparison invalid() {
            return new BenchmarkComparison();
        }

        public boolean isValid() {
            return valid;
        }

        public double getFpsBefore() {
            return fpsBefore;
        }

        public double getFpsAfter() {
            return fpsAfter;
        }

        public double getOnePercentLowBefore() {
            return onePercentLowBefore;
        }

        public double getOnePercentLowAfter() {
            return onePercentLowAfter;
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

        public double getOnePercentLowChange() {
            return onePercentLowChange;
        }

        public double getOnePercentLowChangePercent() {
            return onePercentLowChangePercent;
        }

        public double getFrameTimeChange() {
            return frameTimeChange;
        }

        public double getFrameTimeChangePercent() {
            return frameTimeChangePercent;
        }

        public boolean improved() {

            return fpsChange > 0
                    && frameTimeChange < 0;
        }

        public boolean fpsImproved() {
            return fpsChange > 0;
        }

        public boolean frametimeImproved() {
            return frameTimeChange < 0;
        }

        public boolean onePercentLowImproved() {
            return onePercentLowChange > 0;
        }

        public boolean significantlyImproved() {

            return fpsChangePercent >= 2.0
                    || onePercentLowChangePercent >= 2.0;
        }

        public boolean degraded() {

            return fpsChangePercent <= -2.0
                    || onePercentLowChangePercent <= -2.0;
        }
    }
}
