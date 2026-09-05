package com.hyperframe.optimization;

public interface Optimization {

    String getId();

    String getName();

    String getDescription();

    boolean isApplicable();

    boolean isEnabled();

    void apply();

    void rollback();

    double getPriority();

    OptimizationCategory getCategory();

    enum OptimizationCategory {
        CPU,
        GPU,
        MEMORY,
        CHUNKS,
        ENTITIES,
        PARTICLES,
        RENDERING,
        GENERAL
    }
}
