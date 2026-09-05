package com.hyperframe.modes;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ModeProfile {

    private final PerformanceMode mode;
    private final Set<String> preferredOptimizations;

    private final boolean aggressive;
    private final boolean preserveQuality;
    private final boolean prioritizeStability;

    public ModeProfile(
            PerformanceMode mode,
            Set<String> preferredOptimizations,
            boolean aggressive,
            boolean preserveQuality,
            boolean prioritizeStability
    ) {
        this.mode = mode;
        this.preferredOptimizations =
                new LinkedHashSet<>(preferredOptimizations);
        this.aggressive = aggressive;
        this.preserveQuality = preserveQuality;
        this.prioritizeStability = prioritizeStability;
    }

    public PerformanceMode getMode() {
        return mode;
    }

    public Set<String> getPreferredOptimizations() {
        return Collections.unmodifiableSet(
                preferredOptimizations
        );
    }

    public boolean isAggressive() {
        return aggressive;
    }

    public boolean shouldPreserveQuality() {
        return preserveQuality;
    }

    public boolean prioritizesStability() {
        return prioritizeStability;
    }

    public boolean prefers(String optimizationId) {
        return preferredOptimizations.contains(
                optimizationId
        );
    }
}
