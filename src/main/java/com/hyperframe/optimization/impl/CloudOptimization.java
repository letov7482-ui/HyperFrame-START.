package com.hyperframe.optimization.impl;

import com.hyperframe.optimization.Optimization;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;

public final class CloudOptimization implements Optimization {

    private final MinecraftClient client;

    private CloudRenderMode originalValue;
    private boolean applied;

    public CloudOptimization(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public String getId() {
        return "clouds";
    }

    @Override
    public String getName() {
        return "Cloud Optimization";
    }

    @Override
    public String getDescription() {
        return "Disables cloud rendering to reduce GPU workload.";
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

        originalValue =
                client.options.getCloudRenderModeValue();

        client.options.setCloudRenderMode(
                CloudRenderMode.OFF
        );

        applied = true;
    }

    @Override
    public void rollback() {
        if (!applied || originalValue == null) {
            return;
        }

        client.options.setCloudRenderMode(originalValue);
        applied = false;
    }

    @Override
    public double getPriority() {
        return 4.0;
    }

    @Override
    public OptimizationCategory getCategory() {
        return OptimizationCategory.GPU;
    }
}
