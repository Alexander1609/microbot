package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class RFDStartTask extends AccountBuilderQuestTask {
    public RFDStartTask(){
        super(QuestHelperQuest.RECIPE_FOR_DISASTER_START);

        ignoreUntradables = true;
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory())
            return false;

        if (!Rs2Inventory.contains(ItemID.ROTTEN_TOMATO)) {
            if (!Rs2Inventory.contains("Coins")) {
                if (!Rs2Bank.walkToBankAndUseBank())
                    return false;

                Rs2Bank.withdrawX("Coins", 10);
                return false;
            }

            if (!Rs2Walker.walkTo(new WorldPoint(3227, 3410, 0)))
                return false;

            if (!Rs2Shop.isOpen())
                Rs2GameObject.interact("Crate", "Buy");
            else
                Rs2Shop.buyItem("Rotten tomato", "1");
            return false;
        }

        return withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        while (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1855, 5312, 17, 47, 0)) == 0){
            Rs2GameObject.interact("Barrier", "Pass-trough");
            Rs2Player.waitForWalking();
        }
    }
}
