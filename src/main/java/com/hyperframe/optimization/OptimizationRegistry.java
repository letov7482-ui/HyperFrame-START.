package com.hyperframe.optimization;

import com.hyperframe.optimization.impl.CloudOptimization;
import com.hyperframe.optimization.impl.EntityDistanceOptimization;
import com.hyperframe.optimization.impl.ParticleOptimization;
import net.minecraft.client.MinecraftClient;

import java.util.Collections;
import java.util.List;

public final class OptimizationRegistry {

    private static OptimizationRegistry instance;

    private final OptimizationEngine engine;
    private final MinecraftClient client;

    private OptimizationRegistry(
            MinecraftClient client,
            OptimizationEngine engine
    ) {
        this.client = client;
        this.engine = engine;
    }

    public static void initialize(
            MinecraftClient client,
            OptimizationEngine engine
    ) {
        if (instance != null) {
            return;
        }

        instance = new OptimizationRegistry(
                client,
                engine
        );

        instance.registerAll();
    }

    public static OptimizationRegistry getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "OptimizationRegistry has not been initialized"
            );
        }

        return instance;
    }

    private void registerAll() {
        engine.register(
                new ParticleOptimization(client)
        );

        engine.register(
                new EntityDistanceOptimization(client)
        );

        engine.register(
                new CloudOptimization(client)
        );
    }

    public List<OptimizationEntry> getAll() {
        return engine.getOptimizations();
    }

    public List<OptimizationEntry> getActive() {
        return engine.getActiveOptimizations();
    }

    public OptimizationEntry get(String id) {
        return engine.getEntry(id);
    }

    public int getTotalCount() {
        return engine.getOptimizations().size();
    }

    public int getActiveCount() {
        return engine.getActiveOptimizations().size();
    }

    public List<OptimizationEntry> getByCategory(
            Optimization.OptimizationCategory category
    ) {
        return engine.getOptimizations()
                .stream()
                .filter(entry ->
                        entry.getOptimization()
                                .getCategory() == category
                )
                .toList();
    }

    public List<OptimizationEntry> getInactive() {
        return engine.getOptimizations()
                .stream()
                .filter(entry -> !entry.isActive())
                .toList();
    }

    public boolean contains(String id) {
        return engine.getEntry(id) != null;
    }

    public List<String> getActiveIds() {
        return engine.getActiveOptimizations()
                .stream()
                .map(entry ->
                        entry.getOptimization().getId()
                )
                .toList();
    }

    public void rollbackEverything() {
        engine.rollbackAll();
    }
}
