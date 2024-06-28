package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;

public class GertrudesCatTask extends AccountBuilderQuestTask {
    public GertrudesCatTask(){
        super(QuestHelperQuest.GERTRUDES_CAT);
    }

    @Override
    public void run() {
        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(
                new Restriction(3305, 3501, 0),
                new Restriction(3309, 3499, 0),
                new Restriction(3303, 3505, 0),
                new Restriction(3308, 3507, 0)
        );

        super.run();
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Use your Doogle Leaves")))
            Rs2Inventory.combine(ItemID.RAW_SARDINE, ItemID.DOOGLE_LEAVES);
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyRequiredItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        sleep(5000);
        Rs2Npc.interact("Kitten", "Pick-up");
        Rs2Player.waitForAnimation();
    }
}
