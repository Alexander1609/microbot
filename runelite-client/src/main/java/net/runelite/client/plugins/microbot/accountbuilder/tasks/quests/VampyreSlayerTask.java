package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
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

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VampyreSlayerTask extends AccountBuilderQuestTask {
    public VampyreSlayerTask(){
        super(QuestHelperQuest.VAMPYRE_SLAYER, false);
        useFood = true;
        memberOnly = false;
    }

    String food = "Shrimps";

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.DR_HARLOW && !Rs2Inventory.contains("beer") && Rs2Inventory.hasItemAmount("Coins", 3, true)
                && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3218, 3394, 15, 9, 0)) == 0){
            if (isQuestRunning())
                stopQuest();

            if (ShortestPathPlugin.getMarker() != null)
                ShortestPathPlugin.exit();

            if (!Rs2Dialogue.isInDialogue()){
                Rs2Npc.interact("Bartender", "Talk-to");
            } else if(Rs2Dialogue.hasSelectAnOption()){
                Rs2Widget.clickWidget("finest ale");
            } else {
                Rs2Dialogue.clickContinue();
            }
        } else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.LARGE_DOOR_134 && !Rs2Inventory.hasItem("Hammer")){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(new WorldArea(3214, 3411, 7, 8, 0), 0))
                return;

            if (ShortestPathPlugin.getMarker() != null)
                ShortestPathPlugin.exit();

            Rs2Shop.openShop("Shop keeper");
            Rs2Shop.buyItem("Hammer", "1");
        } else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20;
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.count(food) < 20 && !getFoodFromBank())
            return false;

        if (!Rs2Equipment.isWearing("Training sword") || !Rs2Equipment.isWearing("Training shield")){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                return false;

            Rs2Bank.depositEquipment();
            Rs2Bank.withdrawAndEquip("Training sword");
            Rs2Bank.withdrawAndEquip("Training shield");

            return false;
        }

        if (!Rs2Inventory.hasItemAmount("Coins", 3, true)){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                return false;

            Rs2Bank.withdrawX("Coins", 3);

            return false;
        }

        return true;
    }

    private boolean getFoodFromBank(){
        if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
            return false;

        Rs2Bank.depositAll();

        if (!Rs2Bank.hasBankItem(food, 20)){
            cancel();
            return false;
        } else {
            Rs2Bank.withdrawX(food, 20);
            return true;
        }
    }
}
