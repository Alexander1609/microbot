package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

import java.util.concurrent.TimeUnit;

public class WitchsPotionTask extends AccountBuilderQuestTask {
    public WitchsPotionTask(){
        super(QuestHelperQuest.WITCHS_POTION, false);
        memberOnly = false;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.RAT_2855){
            if (!Rs2Inventory.contains(ItemID.EYE_OF_NEWT)){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Walker.walkTo(new WorldPoint(3013, 3258, 0), 2))
                    return;

                if (!Rs2Shop.isOpen())
                    Rs2Shop.openShop("Betty");
                else
                    Rs2Shop.buyItem("Eye of newt", "1");
            } else if (!Rs2Inventory.contains(ItemID.BURNT_MEAT)){
                if (!Rs2Inventory.contains(ItemID.RAW_RAT_MEAT) && !Rs2Inventory.contains(ItemID.COOKED_MEAT)){
                    if (Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2998, 3186, 0)) < 10){
                        if (Rs2GroundItem.loot(ItemID.RAW_RAT_MEAT))
                            return;
                        else if (!Rs2Combat.inCombat())
                            Rs2Npc.interact("Giant rat", "Attack");

                        return;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(2998, 3186, 0), 2))
                        return;
                } else if (!Rs2Walker.walkTo(new WorldPoint(2968, 3206, 0), 3))
                    return;
                else {
                    if (Rs2Inventory.useItemOnObject(ItemID.COOKED_MEAT, 24969)){
                        Rs2Player.waitForWalking();
                        Rs2Widget.clickWidget("Cooked meat");
                        Rs2Player.waitForAnimation();
                    }
                    else if (Rs2Inventory.useItemOnObject(ItemID.RAW_RAT_MEAT, 24969)){
                        Rs2Player.waitForWalking();
                        Rs2Widget.clickWidget("Raw rat meat");
                        Rs2Player.waitForAnimation();
                    }
                }
            } else if (!Rs2Inventory.contains(ItemID.ONION)){
                if (!Rs2Walker.walkTo(new WorldPoint(2951, 3250, 0), 2))
                    return;

                Rs2GameObject.interact("Onion", "Pick");
                Rs2Player.waitForAnimation();
            } else if (!isQuestRunning())
                startupQuest();

            if (Rs2GroundItem.exists(ItemID.RATS_TAIL, 3))
                Rs2GroundItem.loot(ItemID.RATS_TAIL);
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 10){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
        }

        if (!Rs2Inventory.hasItemAmount("Coins", 3, true)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawX("Coins", 3);
            return false;
        }

        return true;
    }
}
