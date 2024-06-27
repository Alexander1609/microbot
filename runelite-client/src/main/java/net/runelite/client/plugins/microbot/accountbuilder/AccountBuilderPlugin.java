package net.runelite.client.plugins.microbot.accountbuilder;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "AIO Account builder",
        description = "Microbot example plugin",
        tags = {"example", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class AccountBuilderPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;
    @Inject
    private AccountBuilderConfig config;

    @Provides
    AccountBuilderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AccountBuilderConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AccountBuilderOverlay exampleOverlay;

    @Inject
    AccountBuilderScript script;


    @Override
    protected void startUp() throws AWTException {
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());

        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(exampleOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        script.onChatMessage(chatMessage);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event){
        script.onGameObjectSpawned(event);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        script.onGameTick(gameTick);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        script.onGameStateChanged(event);
    }
}
