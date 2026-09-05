package com.hyperframe.optimization;

import com.hyperframe.core.HyperFrameCore;
import com.hyperframe.core.PerformanceSnapshot;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

public final class OptimizationEngine {

    private static OptimizationEngine instance;

    private final MinecraftClient client;
    private final HyperFrameCore core;

    private final List<OptimizationEntry> optimizations =
            new ArrayList<>();

    private boolean optimizing;
    private String currentOptimizationId = "";

    private OptimizationEngine(
            MinecraftClient client,
            HyperFrameCore core
    ) {
        this.client = client;
        this.core = core;
    }

    public static void initialize(
            MinecraftClient client,
            HyperFrameCore core
    ) {
        if (instance == null) {
            instance = new OptimizationEngine(
                    client,
                    core
            );
        }
    }

    public static OptimizationEngine getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "OptimizationEngine has not been initialized"
            );
        }

        return instance;
    }

    public void register(Optimization optimization) {
        if (optimization == null) {
            return;
        }

        for (OptimizationEntry entry : optimizations) {
            if (entry.getOptimization()
                    .getId()
                    .equals(optimization.getId())) {
                return;
            }
        }

        OptimizationEntry entry =
                new OptimizationEntry(optimization);

        entry.setState(OptimizationState.READY);

        optimizations.add(entry);

        sortOptimizations();
    }

    private void sortOptimizations() {
        optimizations.sort(
                Comparator.comparingDouble(
                        entry -> -entry.getOptimization().getPriority()
                )
        );
    }

    public void tick() {
        if (!optimizing) {
            return;
        }

        if (currentOptimizationId.isEmpty()) {
            optimizing = false;
        }
    }

    public OptimizationResult test(
            Optimization optimization
    ) {
        if (optimization == null) {
            return null;
        }

        OptimizationEntry entry =
                findEntry(optimization.getId());

        if (entry == null) {
            register(optimization);
            entry = findEntry(optimization.getId());
        }

        if (entry == null) {
            return null;
        }

        PerformanceSnapshot before =
                core.getPerformanceSnapshot();

        double fpsBefore =
                core.getPerformanceMonitor().getAverageFps();

        double frameTimeBefore =
                before.getFrameTime();

        long start =
                System.currentTimeMillis();

        entry.setState(
                OptimizationState.ANALYZING
        );

        if (!optimization.isApplicable()) {
            entry.setState(
                    OptimizationState.INEFFECTIVE
            );

            return new OptimizationResult(
                    optimization.getId(),
                    false,
                    fpsBefore,
                    fpsBefore,
                    frameTimeBefore,
                    frameTimeBefore,
                    System.currentTimeMillis() - start,
                    "Optimization is not applicable"
            );
        }

        try {
            entry.setState(
                    OptimizationState.APPLYING
            );

            optimization.apply();

            entry.setState(
                    OptimizationState.TESTING
            );

            PerformanceSnapshot after =
                    core.getPerformanceSnapshot();

            double fpsAfter =
                    core.getPerformanceMonitor().getAverageFps();

            double frameTimeAfter =
                    after.getFrameTime();

            boolean improved =
                    fpsAfter > fpsBefore
                            || frameTimeAfter < frameTimeBefore;

            OptimizationResult result =
                    new OptimizationResult(
                            optimization.getId(),
                            improved,
                            fpsBefore,
                            fpsAfter,
                            frameTimeBefore,
                            frameTimeAfter,
                            System.currentTimeMillis() - start,
                            improved
                                    ? "Performance improved"
                                    : "No measurable improvement"
                    );

            entry.setLastResult(result);

            if (improved) {
                entry.setState(
                        OptimizationState.ACTIVE
                );
            } else {
                optimization.rollback();

                entry.setState(
                        OptimizationState.ROLLED_BACK
                );
            }

            return result;

        } catch (Throwable throwable) {

            try {
                optimization.rollback();
            } catch (Throwable ignored) {
            }

            entry.setState(
                    OptimizationState.FAILED
            );

            OptimizationResult result =
                    new OptimizationResult(
                            optimization.getId(),
                            false,
                            fpsBefore,
                            fpsBefore,
                            frameTimeBefore,
                            frameTimeBefore,
                            System.currentTimeMillis() - start,
                            "Optimization failed: "
                                    + throwable.getClass()
                                    .getSimpleName()
                    );

            entry.setLastResult(result);

            return result;
        }
    }

    public void apply(Optimization optimization) {
        if (optimization == null) {
            return;
        }

        OptimizationEntry entry =
                findEntry(optimization.getId());

        if (entry == null) {
            register(optimization);
            entry = findEntry(optimization.getId());
        }

        if (entry == null) {
            return;
        }

        try {
            entry.setState(
                    OptimizationState.APPLYING
            );

            optimization.apply();

            entry.setState(
                    OptimizationState.ACTIVE
            );

        } catch (Throwable throwable) {

            try {
                optimization.rollback();
            } catch (Throwable ignored) {
            }

            entry.setState(
                    OptimizationState.FAILED
            );
        }
    }

    public void rollback(Optimization optimization) {
        if (optimization == null) {
            return;
        }

        OptimizationEntry entry =
                findEntry(optimization.getId());

        try {
            optimization.rollback();

            if (entry != null) {
                entry.setState(
                        OptimizationState.ROLLED_BACK
                );
            }

        } catch (Throwable throwable) {

            if (entry != null) {
                entry.setState(
                        OptimizationState.FAILED
                );
            }
        }
    }

    private OptimizationEntry findEntry(String id) {
        for (OptimizationEntry entry : optimizations) {
            if (entry.getOptimization()
                    .getId()
                    .equals(id)) {
                return entry;
            }
        }

        return null;
    }

    public List<OptimizationEntry> getOptimizations() {
        return Collections.unmodifiableList(
                optimizations
        );
    }

    public List<OptimizationEntry> getActiveOptimizations() {
        List<OptimizationEntry> active =
                new ArrayList<>();

        for (OptimizationEntry entry : optimizations) {
            if (entry.isActive()) {
                active.add(entry);
            }
        }

        return Collections.unmodifiableList(active);
    }

    public OptimizationEntry getEntry(String id) {
        return findEntry(id);
    }

    public boolean isOptimizing() {
        return optimizing;
    }

    public String getCurrentOptimizationId() {
        return currentOptimizationId;
    }

    public void startOptimizationSession() {
        optimizing = true;
        currentOptimizationId = "";
    }

    public void stopOptimizationSession() {
        optimizing = false;
        currentOptimizationId = "";
    }

    public void setCurrentOptimization(String id) {
        currentOptimizationId =
                id == null ? "" : id;
    }

    public void rollbackAll() {
        for (OptimizationEntry entry : optimizations) {
            if (entry.isActive()) {
                rollback(
                        entry.getOptimization()
                );
            }
        }
    }
          }
