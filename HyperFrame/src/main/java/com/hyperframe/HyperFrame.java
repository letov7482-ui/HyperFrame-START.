package com.hyperframe;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyperFrame implements ModInitializer {

    public static final String MOD_ID = "hyperframe";
    public static final String MOD_NAME = "HyperFrame";

    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("========================================");
        LOGGER.info("        HyperFrame is starting...");
        LOGGER.info("        Intelligent FPS Optimizer");
        LOGGER.info("        Minecraft 1.21.11");
        LOGGER.info("========================================");
    }
}
