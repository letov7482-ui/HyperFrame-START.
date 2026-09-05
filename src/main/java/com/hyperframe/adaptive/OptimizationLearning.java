package com.hyperframe.adaptive;

public final class OptimizationLearning {

    private final String optimizationId;

    private int attempts;
    private int improvements;
    private int degradations;
    private int rollbacks;

    private double totalFpsChangePercent;
    private double totalOnePercentLowChangePercent;

    private double bestFpsChangePercent;
    private double bestOnePercentLowChangePercent;

    public OptimizationLearning(
            String optimizationId
    ) {
        this.optimizationId = optimizationId;
    }

    public void recordSuccess(
            double fpsChangePercent,
            double onePercentLowChangePercent
    ) {
        attempts++;
        improvements++;

        totalFpsChangePercent += fpsChangePercent;
        totalOnePercentLowChangePercent +=
                onePercentLowChangePercent;

        bestFpsChangePercent =
                Math.max(
                        bestFpsChangePercent,
                        fpsChangePercent
                );

        bestOnePercentLowChangePercent =
                Math.max(
                        bestOnePercentLowChangePercent,
                        onePercentLowChangePercent
                );
    }

    public void recordDegradation(
            double fpsChangePercent,
            double onePercentLowChangePercent
    ) {
        attempts++;
        degradations++;

        totalFpsChangePercent += fpsChangePercent;
        totalOnePercentLowChangePercent +=
                onePercentLowChangePercent;
    }

    public void recordRollback() {
        rollbacks++;
    }

    public String getOptimizationId() {
        return optimizationId;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getImprovements() {
        return improvements;
    }

    public int getDegradations() {
        return degradations;
    }

    public int getRollbacks() {
        return rollbacks;
    }

    public double getAverageFpsChangePercent() {
        if (attempts == 0) {
            return 0.0;
        }

        return totalFpsChangePercent
                / attempts;
    }

    public double getAverageOnePercentLowChangePercent() {
        if (attempts == 0) {
            return 0.0;
        }

        return totalOnePercentLowChangePercent
                / attempts;
    }

    public double getBestFpsChangePercent() {
        return bestFpsChangePercent;
    }

    public double getBestOnePercentLowChangePercent() {
        return bestOnePercentLowChangePercent;
    }

    public double getSuccessRate() {
        if (attempts == 0) {
            return 0.0;
        }

        return improvements
                / (double) attempts;
    }

    public boolean hasHistory() {
        return attempts > 0;
    }

    public boolean shouldAvoid() {
        return attempts >= 3
                && successRate() < 0.34;
    }

    private double successRate() {
        return getSuccessRate();
    }
}
