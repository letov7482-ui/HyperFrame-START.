package com.hyperframe.modes;

public enum PerformanceMode {

    SMART(
            "Smart",
            "Automatically selects optimizations based on current workload."
    ),

    MAX_FPS(
            "Max FPS",
            "Aggressive performance profile focused on maximum frame rate."
    ),

    SUSTAINED(
            "Sustained",
            "Keeps performance stable during long gaming sessions."
    ),

    QUALITY(
            "Quality",
            "Improves performance while preserving visual quality."
    );

    private final String displayName;
    private final String description;

    PerformanceMode(
            String displayName,
            String description
    ) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
