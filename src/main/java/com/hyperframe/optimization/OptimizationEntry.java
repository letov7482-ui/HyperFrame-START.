package com.hyperframe.optimization;

public final class OptimizationEntry {

    private final Optimization optimization;

    private OptimizationState state =
            OptimizationState.DISABLED;

    private OptimizationResult lastResult;

    private int successfulRuns;
    private int failedRuns;

    public OptimizationEntry(Optimization optimization) {
        this.optimization = optimization;
    }

    public Optimization getOptimization() {
        return optimization;
    }

    public OptimizationState getState() {
        return state;
    }

    public void setState(OptimizationState state) {
        this.state = state;
    }

    public OptimizationResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(OptimizationResult result) {
        this.lastResult = result;

        if (result != null && result.isSuccessful()) {
            successfulRuns++;
        } else {
            failedRuns++;
        }
    }

    public int getSuccessfulRuns() {
        return successfulRuns;
    }

    public int getFailedRuns() {
        return failedRuns;
    }

    public boolean isActive() {
        return state == OptimizationState.ACTIVE;
    }
}
