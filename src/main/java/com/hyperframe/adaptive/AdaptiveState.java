package com.hyperframe.adaptive;

public enum AdaptiveState {

    IDLE,
    ANALYZING,
    SELECTING,
    BASELINE,
    APPLYING,
    TESTING,
    COMPARING,
    KEEPING,
    ROLLING_BACK,
    COOLDOWN,
    FINISHED,
    FAILED
}
