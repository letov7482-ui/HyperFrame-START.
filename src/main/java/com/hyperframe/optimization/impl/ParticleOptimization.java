package com.hyperframe.optimization.impl;

import com.hyperframe.optimization.Optimization;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public final class ParticleOptimization implements Optimization {

    private final MinecraftClient client;

    private GameOptions.Particles originalValue;
    private boolean applied;

    public ParticleOptimization(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public String getId() {
        return "particles";
    }

    @Override
    public String getName() {
        return "Particle Optimization";
    }

    @Override
    public String getDescription() {
        return "Reduces particle rendering to lower rendering overhead.";
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

        originalValue = client.options.getParticles().getValue();

        client.options.getParticles().setValue(
                GameOptions.Particles.MINIMAL
        );

        applied = true;
    }

    @Override
    public void rollback() {
        if (!applied || originalValue == null) {
            return;
        }

        client.options.getParticles().setValue(originalValue);
        applied = false;
    }

    @Override
    public double getPriority() {
        return 7.0;
    }

    @Override
    public OptimizationCategory getCategory() {
        return OptimizationCategory.PARTICLES;
    }
}
