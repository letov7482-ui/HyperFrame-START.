package com.hyperframe.modes;

import com.hyperframe.core.HyperFrameCore;
import com.hyperframe.core.WorkloadAnalyzer;
import com.hyperframe.optimization.OptimizationEntry;
import com.hyperframe.optimization.OptimizationEngine;
import com.hyperframe.optimization.OptimizationRegistry;

import java.util.ArrayList;
import java.util.List;

public final class ModeEngine {

    private static ModeEngine instance;

    private final HyperFrameCore core;
    private final OptimizationEngine optimizationEngine;
    private final OptimizationRegistry registry;

    private PerformanceMode currentMode =
            PerformanceMode.SMART;

    private ModeProfile currentProfile =
            ModeProfiles.create(PerformanceMode.SMART);

    private ModeEngine(
            HyperFrameCore core,
            OptimizationEngine optimizationEngine,
            OptimizationRegistry registry
    ) {
        this.core = core;
        this.optimizationEngine = optimizationEngine;
        this.registry = registry;
    }

    public static void initialize(
            HyperFrameCore core,
            OptimizationEngine optimizationEngine,
            OptimizationRegistry registry
    ) {
        if (instance != null) {
            return;
        }

        instance = new ModeEngine(
                core,
                optimizationEngine,
                registry
        );
    }

    public static ModeEngine getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "ModeEngine has not been initialized"
            );
        }

        return instance;
    }

    public void tick() {
        if (currentMode != PerformanceMode.SMART) {
            return;
        }

        runSmartAnalysis();
    }

    public void setMode(
            PerformanceMode mode
    ) {
        if (mode == null || mode == currentMode) {
            return;
        }

        optimizationEngine.rollbackAll();

        currentMode = mode;
        currentProfile = ModeProfiles.create(mode);

        if (mode == PerformanceMode.SMART) {
            return;
        }

        applyProfile(currentProfile);
    }

    private void runSmartAnalysis() {
        WorkloadAnalyzer analyzer =
                core.getWorkloadAnalyzer();

        switch (analyzer.getWorkloadType()) {

            case IDLE:
            case LIGHT:
                applySmartLightProfile();
                break;

            case MODERATE:
                applySmartModerateProfile();
                break;

            case HEAVY:
            case EXTREME:
                applySmartHeavyProfile();
                break;
        }
    }

    private void applySmartLightProfile() {
        rollbackIfActive("entity_distance");
        rollbackIfActive("particles");
    }

    private void applySmartModerateProfile() {
        applyIfInactive("particles");
    }

    private void applySmartHeavyProfile() {
        applyIfInactive("particles");
        applyIfInactive("entity_distance");
    }

    private void applyProfile(
            ModeProfile profile
    ) {
        for (String id :
                profile.getPreferredOptimizations()) {

            OptimizationEntry entry =
                    registry.get(id);

            if (entry == null) {
                continue;
            }

            if (!entry.isActive()) {
                optimizationEngine.apply(
                        entry.getOptimization()
                );
            }
        }
    }

    private void applyIfInactive(String id) {
        OptimizationEntry entry =
                registry.get(id);

        if (entry == null || entry.isActive()) {
            return;
        }

        optimizationEngine.apply(
                entry.getOptimization()
        );
    }

    private void rollbackIfActive(String id) {
        OptimizationEntry entry =
                registry.get(id);

        if (entry == null || !entry.isActive()) {
            return;
        }

        optimizationEngine.rollback(
                entry.getOptimization()
        );
    }

    public PerformanceMode getCurrentMode() {
        return currentMode;
    }

    public ModeProfile getCurrentProfile() {
        return currentProfile;
    }

    public String getModeName() {
        return currentMode.getDisplayName();
    }

    public String getModeDescription() {
        return currentMode.getDescription();
    }

    public List<String> getActiveOptimizationIds() {
        return new ArrayList<>(
                registry.getActiveIds()
        );
    }

    public int getActiveOptimizationCount() {
        return registry.getActiveCount();
    }

    public int getTotalOptimizationCount() {
        return registry.getTotalCount();
    }
}
