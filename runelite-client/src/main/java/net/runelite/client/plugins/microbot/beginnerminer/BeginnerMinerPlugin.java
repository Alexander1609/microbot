package net.runelite.client.plugins.microbot.beginnerminer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Beginner miner",
        description = "Microbot example plugin",
        tags = {"example", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class BeginnerMinerPlugin extends Plugin {
    @Inject
    private BeginnerMinerConfig config;
    @Provides
    BeginnerMinerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BeginnerMinerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BeginnerMinerOverlay exampleOverlay;

    @Inject
    BeginnerMinerScript exampleScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        exampleScript.run(config);
    }

    protected void shutDown() {
        exampleScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
