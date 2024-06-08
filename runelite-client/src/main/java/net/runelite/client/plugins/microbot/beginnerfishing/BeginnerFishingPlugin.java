package net.runelite.client.plugins.microbot.beginnerfishing;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Beginner fishing",
        description = "Microbot example plugin",
        tags = {"example", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class BeginnerFishingPlugin extends Plugin {
    @Inject
    private BeginnerFishingConfig config;
    @Provides
    BeginnerFishingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BeginnerFishingConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BeginnerFishingOverlay exampleOverlay;

    @Inject
    BeginnerFishingScript exampleScript;


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
