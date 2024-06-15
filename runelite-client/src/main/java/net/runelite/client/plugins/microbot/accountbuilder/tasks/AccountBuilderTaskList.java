package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Getter;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.GoblinFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.*;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.*;

import java.util.HashMap;
import java.util.Map;

public class AccountBuilderTaskList {
    @Getter
    private static final Map<AccountBuilderTask, Integer> tasks;

    static {
        tasks = new HashMap<>();

        tasks.put(new CookAssistantTask(), 4000);
        tasks.put(new SheepShearerTask(), 2000);
        tasks.put(new RomeoJulietTask(), 1000);
        tasks.put(new RuneMysteriesTask(), 1000);
        tasks.put(new RestlessGhostTask(), 1000);
        tasks.put(new ErnestTheChickenTask(), 1000);
        tasks.put(new MisthalinMysteryTask(), 1000);

        tasks.put(new GoblinFightingTask(), 1000);

        tasks.put(new MiningSmithingBronzeTask(), 1000);
        tasks.put(new ChoppingTreeTask(), 1000);
        tasks.put(new ChoppingOakTask(), 1000);
        tasks.put(new ChoppingWillowTask(), 1000);
        tasks.put(new FishingShrimpsTask(), 1000);
    }
}
