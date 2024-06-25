package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class BlackKnightFortressTask extends AccountBuilderQuestTask {
    String food = "Shrimps";

    public BlackKnightFortressTask(){
        super(QuestHelperQuest.BLACK_KNIGHTS_FORTRESS);
        useFood = true;
        memberOnly = false;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.STURDY_DOOR){
            if (!Rs2Equipment.isWearing(ItemID.BRONZE_MED_HELM))
                Rs2Inventory.interact(ItemID.BRONZE_MED_HELM);
            else if (!Rs2Equipment.isWearing(ItemID.IRON_CHAINBODY))
                Rs2Inventory.interact(ItemID.IRON_CHAINBODY);
        } else if (step.objectID == ObjectID.LADDER_17148 && step.getWorldPoint().equals(new WorldPoint(3025, 3513, 1))){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3018, 3505, 8, 8, 1)) == 0)
                Rs2Walker.walkTo(new WorldPoint(3028, 3514, 1), 2);
        } else if (step.objectID == ObjectID.LADDER_17159){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3014, 3517, 4, 4, 0)) == 0)
                Rs2GameObject.interact(2341);
        } else if (step.objectID == ObjectID.HOLE_2336) {
            Rs2Inventory.useItemOnObject(ItemID.CABBAGE, ObjectID.HOLE_2336);
        } else if (step.objectID == ObjectID.STAIRCASE_24072){
            boolean stopQuest = true;

            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3025, 3504, 9, 6, 1)) == 0)
                Rs2GameObject.interact(2341);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3025, 3510, 8, 10, 1)) == 0)
                Rs2GameObject.interact(17160);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3020, 3511, 11, 8, 0)) == 0)
                Rs2Walker.walkTo(new WorldPoint(3016, 3513, 0), 2);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3007, 3515, 13, 5, 0)) == 0)
                Rs2Walker.walkTo(new WorldPoint(3016, 3513, 0), 2);
            else
                stopQuest = false;

            if (stopQuest && isQuestRunning())
                stopQuest();
            else if (!stopQuest && !isQuestRunning())
                startupQuest();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Inventory.hasItemAmount("Coins", 210, true) && !Rs2Inventory.hasItem(ItemID.IRON_CHAINBODY)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            Rs2Bank.withdrawX("Coins", 210);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount(food, 20)) {
            if (!Rs2Bank.hasBankItem(food, 20)) {
                cancel();
                return false;
            } else {
                Rs2Bank.withdrawX(food, 20);
                return false;
            }
        }

        if (!Rs2Inventory.hasItem(ItemID.BRONZE_MED_HELM)){
            if (!Rs2Walker.walkTo(new WorldPoint(3123, 3357, 0), 1))
                return false;

            Rs2GroundItem.loot(ItemID.BRONZE_MED_HELM);
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.IRON_CHAINBODY)){
            if (!Rs2Walker.walkTo(new WorldPoint(3229, 3437, 0), 5))
                return false;

            if (!Rs2Shop.isOpen())
                Rs2Shop.openShop("Horvik");
            else
                Rs2Shop.buyItem("Iron chainbody", "1");

            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.CABBAGE)){
            if (!Rs2Walker.walkTo(new WorldPoint(3051, 3505, 0), 3))
                return false;

            Rs2GameObject.interact("Cabbage", "Pick");
            Rs2Player.waitForAnimation();
            return false;
        }

        return true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20
                && Microbot.getVarbitPlayerValue(VarPlayer.QUEST_POINTS) >= 12;
    }
}
