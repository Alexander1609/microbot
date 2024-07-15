package net.runelite.client.plugins.microbot.accountbuilder;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.Panel.AccountBuilderPanel;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTaskList;
import net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryState;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.tools.Icon;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Basche + "AIO Account builder",
        description = "Basche's account builder",
        tags = {"Basche", "microbot"},
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

    @Inject
    private ClientToolbar clientToolbar;

    @Provides
    AccountBuilderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AccountBuilderConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AccountBuilderOverlay overlay;

    @Getter
    @Inject
    AccountBuilderScript script;

    NavigationButton navButton;

    @Override
    protected void startUp() throws AWTException {
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());

        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        script.run(config);

        clientThread.invokeLater(() -> {
            if (QuestHelperQuest.getQuestHelpers(false).stream().anyMatch(x -> !x.isHasInitialized()))
                return false;

            script.taskMap = AccountBuilderTaskList.getTasks();

            var panel = new AccountBuilderPanel(this);
            navButton = NavigationButton.builder()
                    .tooltip("Account builder")
                    .icon(ImageUtil.loadImageResource(AccountBuilderPlugin.class, "nav_icon.png"))
                    .priority(1000)
                    .panel(panel)
                    .build();

            clientToolbar.addNavigation(navButton);

            return true;
        });
    }

    protected void shutDown() {
        script.shutdown();
        overlayManager.remove(overlay);
        clientToolbar.removeNavigation(navButton);
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

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        script.onWallObjectSpawned(event);
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        script.onHitsplatApplied(hitsplatApplied);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        script.onVarbitChanged(event);
    }
}
