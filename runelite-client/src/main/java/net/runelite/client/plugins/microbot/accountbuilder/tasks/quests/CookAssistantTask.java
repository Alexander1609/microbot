package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class CookAssistantTask extends AccountBuilderQuestTask {
    boolean bankDone;
    boolean depositedWheat;

    public CookAssistantTask(){
        super(QuestHelperQuest.COOKS_ASSISTANT, false);
        memberOnly = false;
    }

    @Override
    public void tick() {
        if (Rs2Widget.hasWidget("What's wrong?"))
            Rs2Widget.clickWidget("What's wrong?");
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

        return true;
    }
}
