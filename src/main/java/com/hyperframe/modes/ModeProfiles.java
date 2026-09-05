package com.hyperframe.modes;

import java.util.LinkedHashSet;

public final class ModeProfiles {

    private ModeProfiles() {
    }

    public static ModeProfile create(
            PerformanceMode mode
    ) {
        return switch (mode) {

            case SMART -> new ModeProfile(
                    PerformanceMode.SMART,
                    new LinkedHashSet<>(),
                    false,
                    true,
                    true
            );

            case MAX_FPS -> new ModeProfile(
                    PerformanceMode.MAX_FPS,
                    new LinkedHashSet<>(
                            java.util.List.of(
                                    "particles",
                                    "entity_distance",
                                    "clouds"
                            )
                    ),
                    true,
                    false,
                    false
            );

            case SUSTAINED -> new ModeProfile(
                    PerformanceMode.SUSTAINED,
                    new LinkedHashSet<>(
                            java.util.List.of(
                                    "particles",
                                    "entity_distance"
                            )
                    ),
                    false,
                    true,
                    true
            );

            case QUALITY -> new ModeProfile(
                    PerformanceMode.QUALITY,
                    new LinkedHashSet<>(
                            java.util.List.of(
                                    "clouds"
                            )
                    ),
                    false,
                    true,
                    false
            );
        };
    }
}
