package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;

public class RestlessGhostTask extends AccountBuilderQuestTask {
    public RestlessGhostTask(){
        super(QuestHelperQuest.THE_RESTLESS_GHOST);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Player.getWorldLocation().distanceTo(new WorldPoint(3105, 3166, 0)) < 2)
            Rs2GameObject.interact(Rs2Walker.getTile(new WorldPoint(3109, 3167, 0)).getWallObject());

        if (Rs2Inventory.hasItem(ItemID.GHOSTSPEAK_AMULET)) {
            Rs2Tab.switchToInventoryTab();
            Rs2Widget.clickWidget("ghostspeak amulet");
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 2){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank())
                return false;

            Rs2Bank.depositAll();

            return false;
        }

        return true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 10;
    }
}
