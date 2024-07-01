package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class SleepingGiantsTask extends AccountBuilderQuestTask {
    public SleepingGiantsTask(){
        super(QuestHelperQuest.SLEEPING_GIANTS,
                new ItemRequirement("Bronze nails", ItemID.BRONZE_NAILS, 10));
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == NullObjectID.NULL_44776){
            if (Rs2Inventory.hasItem("Bronze bar") && Rs2Widget.isWidgetVisible(270, 14))
                Rs2Widget.clickWidget(270, 14);
            else if (Rs2Inventory.hasItem("Iron bar") && Rs2Widget.isWidgetVisible(270, 15))
                Rs2Widget.clickWidget(270, 15);
            else if (Rs2Inventory.hasItem("Bronze")){
                var item = Rs2Inventory.get("Bronze");
                Rs2Inventory.useItemOnObject(item.id, NullObjectID.NULL_44776);
                sleepUntil(() -> Rs2Widget.hasWidget("All"), 2000);
                Rs2Widget.clickWidget("All");
            } else if (Rs2Inventory.hasItem("Iron")){
                var item = Rs2Inventory.get("Iron");
                Rs2Inventory.useItemOnObject(item.id, NullObjectID.NULL_44776);
                sleepUntil(() -> Rs2Widget.hasWidget("All"), 2000);
                Rs2Widget.clickWidget("All");
            }
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        while (!Rs2Walker.walkTo(new WorldPoint(3364, 3147, 0)))
            sleep(500, 1000);
    }
}
