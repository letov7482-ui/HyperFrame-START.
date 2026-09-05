package com.hyperframe;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HyperFrame implements ModInitializer {

    public static final String MOD_ID = "hyperframe";
    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("HyperFrame initialized!");
    }
}
