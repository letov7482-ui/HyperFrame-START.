package com.hyperframe.optimization.impl;

import com.hyperframe.optimization.Optimization;
import net.minecraft.client.MinecraftClient;

public final class EntityDistanceOptimization implements Optimization {

    private final MinecraftClient client;

    private Integer originalDistance;
    private boolean applied;

    public EntityDistanceOptimization(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public String getId() {
        return "entity_distance";
    }

    @Override
    public String getName() {
        return "Entity Distance";
    }

    @Override
    public String getDescription() {
        return "Reduces entity render distance to lower entity rendering cost.";
    }

    @Override
    public boolean isApplicable() {
        return client.options != null;
    }

    @Override
    public boolean isEnabled() {
        return applied;
    }

    @Override
    public void apply() {
        if (!isApplicable() || applied) {
            return;
        }

        originalDistance =
                client.options.getEntityDistance().getValue();

        int optimized =
                Math.max(25, originalDistance - 15);

        client.options.getEntityDistance().setValue(optimized);

        applied = true;
    }

    @Override
    public void rollback() {
        if (!applied || originalDistance == null) {
            return;
        }

        client.options.getEntityDistance().setValue(originalDistance);
        applied = false;
    }

    @Override
    public double getPriority() {
        return 6.0;
    }

    @Override
    public OptimizationCategory getCategory() {
        return OptimizationCategory.ENTITIES;
    }
}
