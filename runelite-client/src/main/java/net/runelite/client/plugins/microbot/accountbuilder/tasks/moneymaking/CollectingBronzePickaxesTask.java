package net.runelite.client.plugins.microbot.accountbuilder.tasks.moneymaking;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

import java.util.Arrays;

public class CollectingBronzePickaxesTask extends AccountBuilderTask {
    boolean pickingUp = false;

    public CollectingBronzePickaxesTask(){
        minTickTime = 100;
        maxTickTime = 250;
        memberOnly = false;
    }

    @Override
    public String getName() {
        return "Collecting bronze pickaxes";
    }

    @Override
    public void tick() {
        if (Rs2Inventory.getEmptySlots() == 0
                || pickingUp && !Rs2GroundItem.exists(ItemID.BRONZE_PICKAXE, 2) && Rs2Inventory.getEmptySlots() != 28){
            if (Rs2Bank.useBank())
                Rs2Bank.depositAll();

            sleep(200, 1500);
            if (Rs2Bank.isOpen())
                Rs2Bank.closeBank();
            return;
        }

        if (Rs2Equipment.isWearing(ItemID.BRONZE_PICKAXE)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return;

            Rs2Bank.depositEquipment();
            return;
        }

        if (pickingUp && Rs2GroundItem.exists(ItemID.BRONZE_PICKAXE, 2) || Arrays.stream(Rs2GroundItem.getAll(3)).filter(x -> x.getItem().getId() == ItemID.BRONZE_PICKAXE).count() + Rs2Inventory.count() == 28){
            pickingUp = true;
            Rs2GroundItem.loot(ItemID.BRONZE_PICKAXE);
            sleep(100, 250);
            return;
        }

        pickingUp = false;

        if (Rs2Inventory.hasItem(ItemID.BRONZE_PICKAXE)){
            Rs2Inventory.drop(ItemID.BRONZE_PICKAXE);
            sleep(100, 250);
        }

        Rs2GameObject.interact(41593, "Take-pickaxe");
        sleepUntil(() -> Rs2Inventory.hasItem(ItemID.BRONZE_PICKAXE), 2000);
        sleep(100, 250);
    }

    @Override
    public boolean requirementsMet() {
        // Travel to dungeon not working for members due to transports
        return super.requirementsMet() && isQuestCompleted(QuestHelperQuest.BELOW_ICE_MOUNTAIN)
                && !Rs2Bank.hasBankItem("Coins", 500_000);
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Player.isMember()
                && Rs2Player.getWorldLocation().distanceTo(new WorldArea(2893, 5757, 154, 106, 0)) != 0){
            if (!Rs2Walker.walkTo(new WorldArea(2995, 3491, 4, 6, 0), 5))
                return false;

            Rs2GameObject.interact(41357, "Enter");
            Rs2Player.waitForWalking();
            return false;
        }

        if (!Rs2Walker.walkTo(new WorldPoint(2976, 5798, 0), 2))
            return false;

        if (Rs2Inventory.getEmptySlots() < 28){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        return true;
    }
}
