package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class PlagueCityTask extends AccountBuilderQuestTask {
    public PlagueCityTask(){
        super(QuestHelperQuest.PLAGUE_CITY);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && isQuestCompleted(QuestHelperQuest.TREE_GNOME_VILLAGE);
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == NullObjectID.NULL_2532){
            if (!Rs2Inventory.contains(ItemID.BUCKET_OF_WATER)){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Inventory.contains(ItemID.BUCKET)){
                    Rs2GroundItem.loot(ItemID.BUCKET);
                    Rs2Player.waitForWalking();
                } else {
                    if (!Rs2Walker.walkTo(new WorldPoint(2572, 3334, 0)))
                        return;

                    Rs2Inventory.useItemOnObject(ItemID.BUCKET, 874);
                    Rs2Player.waitForAnimation();
                }
            } else if (!isQuestRunning())
                startupQuest();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyRequiredItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        Rs2Inventory.interact(ItemID.ARDOUGNE_TELEPORT_SCROLL, "Read");
    }
}
