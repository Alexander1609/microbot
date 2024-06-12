package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;

public class CookAssistantTask extends AccountBuilderTask {
    boolean bankDone;
    boolean depositedWheat;

    public CookAssistantTask(){
        quest = QuestHelperQuest.COOKS_ASSISTANT;
    }

    @Override
    public void init() {
        super.init();

        bankDone = false;
        depositedWheat = false;
    }

    @Override
    public boolean doTaskPreparations() {
        if (!bankDone && (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen()))
            return false;

        if (!bankDone){
            Rs2Bank.depositAll();
            Rs2Bank.withdrawItems(1944, 1927, 1933);

            if (!Rs2Inventory.contains("Bucket of milk"))
                Rs2Bank.withdrawOne("Bucket", true);
            if (!Rs2Inventory.contains("Pot of flour"))
                Rs2Bank.withdrawOne("Pot");

            bankDone = true;
        }

        if (!Rs2Inventory.contains("Pot of flour") && !Rs2Inventory.contains("Pot")){
            if (!Rs2Walker.walkTo(new WorldPoint(3208, 3214, 0)))
                return false;

            if (!Rs2GroundItem.exists("Pot", 10)){
                Rs2Player.logout();
                return false;
            }

            Rs2GroundItem.loot("Pot", 10);
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains("Bucket of milk") && !Rs2Inventory.contains("Bucket")){
            if (!Rs2Walker.walkTo(new WorldPoint(3214, 9623, 0)))
                return false;

            if (!Rs2GroundItem.exists("Bucket", 10)){
                Rs2Player.logout();
                return false;
            }

            Rs2GroundItem.loot("Bucket", 10);
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains("Egg")){
            if (!Rs2Walker.walkTo(new WorldPoint(3229, 3299, 0), 3))
                return false;

            Rs2GroundItem.loot("Egg", 10);
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains("Bucket of milk")){
            if (!Rs2Walker.walkTo(new WorldPoint(3255, 3275, 0), 3))
                return false;

            Rs2Inventory.use("Bucket");
            Rs2GameObject.interact("Dairy cow");

            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains("Pot of flour")){
            if (!Rs2Inventory.contains("Grain") && !depositedWheat){
                if (!Rs2Walker.walkTo(new WorldPoint(3161, 3293, 0), 2))
                    return false;

                Rs2GameObject.interact("Wheat", "Pick");
                Rs2Player.waitForAnimation();
                return false;
            }

            if (!depositedWheat){
                if (!Rs2Walker.walkTo(new WorldPoint(3166, 3306, 2)))
                    return false;

                Rs2Player.waitForAnimation(1000);
                Rs2GameObject.interact(24961, "Fill");
                Rs2Player.waitForAnimation();

                Rs2GameObject.interact("Hopper controls", "Operate");
                Rs2Player.waitForAnimation();
                depositedWheat = true;
                return false;
            }

            if (!Rs2Walker.walkTo(new WorldPoint(3166, 3306, 0)))
                return false;

            Rs2GameObject.interact(1781, "Empty");
            Rs2Player.waitForAnimation();

            return false;
        }

        if (!Rs2Walker.walkTo(new WorldPoint(3208, 3214, 0)))
            return false;

        return true;
    }
}
