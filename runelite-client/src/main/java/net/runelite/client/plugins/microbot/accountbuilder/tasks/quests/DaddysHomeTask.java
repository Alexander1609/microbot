package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class DaddysHomeTask extends AccountBuilderQuestTask {
    public DaddysHomeTask(){
        super(QuestHelperQuest.DADDYS_HOME,
                new ItemRequirement("Bronze nails", ItemID.BRONZE_NAILS, 100));
    }

    @Override
    public void run() {
        super.run();

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(new Restriction(3302, 3496, 0));
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Build the "))){
            if (Rs2Widget.isWidgetVisible(458, 4))
                Rs2Widget.clickWidget(458, 4);
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        Rs2Inventory.interact(ItemID.MARLOS_CRATE, "Open");
    }
}
