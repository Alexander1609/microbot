package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Getter;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.GoblinFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.CookAssistantTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.RomeoJulietTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.RuneMysteriesTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.SheepShearerTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.ChoppingOakTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.ChoppingTreeTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.FishingShrimpsTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.MiningSmithingBronzeTask;

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

        tasks.put(new GoblinFightingTask(), 1000);

        tasks.put(new MiningSmithingBronzeTask(), 2000);
        tasks.put(new ChoppingTreeTask(), 2000);
        tasks.put(new ChoppingOakTask(), 2000);
        tasks.put(new FishingShrimpsTask(), 2000);
    }
}
