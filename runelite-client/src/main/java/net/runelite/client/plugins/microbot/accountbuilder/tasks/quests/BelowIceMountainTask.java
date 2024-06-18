package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.*;

import java.util.concurrent.TimeUnit;

public class BelowIceMountainTask extends AccountBuilderTask {
    FoodScript foodScript = new FoodScript();
    PlayerAssistConfig foodConfig = new PlayerAssistConfig() {
        @Override
        public boolean toggleFood() {
            return true;
        }
    };

    String food = "Shrimps";
    boolean depositedWheat = false;
    QuestStep currentStep;

    public BelowIceMountainTask(){
        quest = QuestHelperQuest.BELOW_ICE_MOUNTAIN;
    }

    @Override
    public void run() {
        super.run();

        foodScript.run(foodConfig);

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            var step = quest.getQuestHelper().getCurrentStep();

            while (step instanceof ConditionalStep)
                step = step.getActiveStep();

            if (step != null)
                currentStep = step;

            if (currentStep instanceof NpcEmoteStep){
                if (!Rs2Inventory.contains(ItemID.COOKED_MEAT)){
                    if (isQuestRunning())
                        stopQuest();

                    Rs2GroundItem.loot(ItemID.COOKED_MEAT);
                    Rs2Player.waitForAnimation();
                } else if (!Rs2Inventory.contains(ItemID.BEER)){
                    if (isQuestRunning())
                        stopQuest();

                    Rs2GroundItem.loot(ItemID.BEER);
                    Rs2Player.waitForAnimation();
                } else if (!isQuestRunning())
                    startupQuest();
            } else if (currentStep instanceof DetailedQuestStep && currentStep.getText().stream().anyMatch(x -> x.contains("Use the knife on the bread"))){
                Rs2Inventory.combine(ItemID.KNIFE, ItemID.BREAD);
            } else if (currentStep instanceof NpcStep){
                var npcStep = (NpcStep)currentStep;

                if (npcStep.npcID == NpcID.ANCIENT_GUARDIAN){
                    if (isQuestRunning())
                        stopQuest();

                    if (Rs2GameObject.get("Structural pillar") == null && !isQuestRunning())
                        startupQuest();
                    else {
                        Rs2GameObject.interact("Structural pillar", "Mine");
                        Rs2Player.waitForAnimation();
                    }

                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Inventory.contains(ItemID.BREAD)){
            if (Rs2Bank.hasItem(ItemID.BREAD)){
                if (!Rs2Bank.walkToBankAndUseBank())
                    return false;

                Rs2Bank.withdrawItem(ItemID.BREAD);
            } else {
                if (Rs2Inventory.count("Pot of flour") > 0
                        && Rs2Inventory.count("Pot of flour") == Rs2Inventory.count("Bucket of water")
                        && Rs2Inventory.count("Pot of flour") + Rs2Inventory.count("Bread") == 9){
                    if (!Rs2Widget.hasWidget("Bread dough"))
                        Rs2Inventory.combine("Pot of flour", "Bucket of water");
                    else{
                        Rs2Widget.clickWidget("Bread dough");
                        Rs2Player.waitForAnimation();
                    }

                    return false;
                }

                if (Rs2Inventory.contains("Bread dough")){
                    if (!Rs2Widget.hasWidget("Bread dough"))
                        Rs2GameObject.interact(114, "Cook");
                    else{
                        Rs2Widget.clickWidget("Bread dough");
                        Rs2Player.waitForAnimation();
                    }

                    return false;
                }

                if (Rs2Inventory.getEmptySlots() < (27
                        - Rs2Inventory.count("Pot")
                        - Rs2Inventory.count("Bucket")
                        - Rs2Inventory.count("Grain")
                        - Rs2Inventory.count("Bread"))){
                    if (!Rs2Bank.walkToBankAndUseBank())
                        return false;
                    else
                        Rs2Bank.depositAll();
                }

                if (Rs2Inventory.count("Pot") < 9){
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

                if (Rs2Inventory.count("Bucket") < 9){
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

                if (Rs2Inventory.count("Pot of flour") < 9){
                    if (Rs2Inventory.count("Grain") < 9 && !depositedWheat && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3162, 3303, 9, 8, 2)) != 0){
                        if (!Rs2Walker.walkTo(new WorldPoint(3161, 3293, 0), 2))
                            return false;

                        Rs2GameObject.interact("Wheat", "Pick");
                        Rs2Player.waitForAnimation();
                        return false;
                    }

                    if (!depositedWheat){
                        if (!Rs2Walker.walkTo(new WorldPoint(3166, 3306, 2)))
                            return false;

                        if (Rs2Inventory.contains("Grain")){
                            Rs2GameObject.interact(24961, "Fill");
                            Rs2Player.waitForWalking();
                            Rs2Player.waitForAnimation();

                            Rs2GameObject.interact("Hopper controls", "Operate");
                            Rs2Player.waitForWalking();
                            Rs2Player.waitForAnimation();
                        } else
                            depositedWheat = true;

                        return false;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(3166, 3306, 0)))
                        return false;

                    Rs2GameObject.interact(1781, "Empty");
                    return false;
                }
                if (Rs2Inventory.count("Bucket of water") < 9){
                    if (!Rs2Walker.walkTo(new WorldPoint(3207, 3214, 0)))
                        return false;

                    Rs2Inventory.use(ItemID.BUCKET);
                    Rs2GameObject.interact("Sink");
                    Rs2Player.waitForAnimation();
                }

                return false;
            }
        }

        if (Rs2Inventory.getEmptySlots() < 20){
            if (Rs2Bank.walkToBankAndUseBank())
                Rs2Bank.depositAll();

            return false;
        }

        if (!Rs2Inventory.contains(ItemID.KNIFE)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawItem(ItemID.KNIFE);
        }

        if (!Rs2Inventory.contains(ItemID.BRONZE_PICKAXE)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawItem(ItemID.BRONZE_PICKAXE);
        }

        if (!Rs2Bank.hasBankItem(food, 20)){
            cancel();
            return false;
        } else {
            Rs2Bank.withdrawX(food, 20);
            return true;
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getVarbitPlayerValue(VarPlayer.QUEST_POINTS) >= 16
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20
                && Microbot.getClient().getRealSkillLevel(Skill.MINING) >= 10;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        foodScript.shutdown();
    }
}
