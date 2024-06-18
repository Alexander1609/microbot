package net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting;

import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.cannon.CannonScript;
import net.runelite.client.plugins.microbot.playerassist.combat.*;
import net.runelite.client.plugins.microbot.playerassist.loot.LootScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GoblinFightingTask extends AccountBuilderTask {
    private final AttackNpcScript attackNpc = new AttackNpcScript();
    private final FoodScript foodScript = new FoodScript();
    private final LootScript lootScript = new LootScript();
    private final BuryScatterScript buryScript = new BuryScatterScript();

    private long nextCombatSwap = 0;

    private final PlayerAssistConfig config = new PlayerAssistConfig() {
        @Override
        public boolean toggleCombat() {
            return true;
        }

        @Override
        public String attackableNpcs() {
            return "Goblin,Giant spider";
        }

        @Override
        public boolean toggleFood() {
            return true;
        }

        @Override
        public boolean toggleBuryBones() {
            return true;
        }

        @Override
        public int minPriceOfItemsToLoot() {
            return 500;
        }

        @Override
        public WorldPoint centerLocation() {
            return new WorldPoint(3252, 3234, 0);
        }

        @Override
        public int attackRadius() {
            return 10;
        }

        @Override
        public boolean toggleOnlyLootMyItems() {
            return true;
        }

        @Override
        public boolean toggleDelayedLooting() {
            return true;
        }
    };

    String food = "Shrimps";
    boolean hasTrainingGear = true;
    WorldArea goblinArea = new WorldArea(3246, 3233, 9, 8, 0);

    @Override
    public String getName() {
        return "Fighting: Goblin";
    }

    @Override
    public boolean requirementsMet() {
        return Microbot.getClient().getRealSkillLevel(Skill.ATTACK) < 20
                || Microbot.getClient().getRealSkillLevel(Skill.STRENGTH) < 20
                || Microbot.getClient().getRealSkillLevel(Skill.DEFENCE) < 20;
    }

    @Override
    public void run() {
        attackNpc.run(config);
        foodScript.run(config);
        lootScript.run(config);
        buryScript.run(config);

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            if (Rs2Player.isAnimating() || Rs2Player.isMoving() || Microbot.pauseAllScripts) return;

            if (!Rs2Inventory.hasItem(food)){
                attackNpc.shutdown();
                getFoodFromBank();
            }
            else if (!attackNpc.isRunning())
                attackNpc.run(config);

            if (Rs2Inventory.hasItem(food) && !Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(goblinArea) > 10)
                Rs2Walker.walkTo(goblinArea, 5);

            if (nextCombatSwap < System.currentTimeMillis()){
                nextCombatSwap = System.currentTimeMillis() + 120_000 + new Random().nextInt(180_000);

                var attackLevel = Microbot.getClient().getRealSkillLevel(Skill.ATTACK);
                var strengthLevel = Microbot.getClient().getRealSkillLevel(Skill.STRENGTH);
                var defenceLevel = Microbot.getClient().getRealSkillLevel(Skill.DEFENCE);

                if (!Rs2Widget.isWidgetVisible(WidgetInfo.COMBAT_STYLE_ONE))
                    Rs2Widget.clickWidget(10747956);

                if (attackLevel <= strengthLevel && attackLevel <= defenceLevel)
                    Rs2Combat.setAttackStyle(WidgetInfo.COMBAT_STYLE_ONE);
                else if (strengthLevel <= attackLevel && strengthLevel <= defenceLevel)
                    Rs2Combat.setAttackStyle(WidgetInfo.COMBAT_STYLE_TWO);
                else
                    Rs2Combat.setAttackStyle(WidgetInfo.COMBAT_STYLE_FOUR);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        attackNpc.shutdown();
        foodScript.shutdown();
        lootScript.shutdown();
        buryScript.shutdown();
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

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.count(food) < 20 && !getFoodFromBank())
            return false;

        if (!Rs2Equipment.isWearing("Training sword") || !Rs2Equipment.isWearing("Training shield")){
            if (hasTrainingGear){
                if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                    return false;

                Rs2Bank.depositEquipment();

                if (!Rs2Bank.hasBankItem("Training sword") && !Rs2Bank.hasBankItem("Training shield"))
                    hasTrainingGear = false;
                else {
                    Rs2Bank.withdrawAndEquip("Training sword");
                    Rs2Bank.withdrawAndEquip("Training shield");
                }
            } else {
                if (!Rs2Walker.walkTo(new WorldPoint(3217, 3238, 0), 5))
                    return false;

                if (Rs2Inventory.contains("Training sword")){
                    Rs2Inventory.equip("Training shield");
                    Rs2Inventory.equip("Training sword");

                    return false;
                }

                if (!Rs2Dialogue.isInDialogue()){
                    Rs2Npc.interact(NpcID.MELEE_COMBAT_TUTOR);
                    sleepUntil(Rs2Dialogue::isInDialogue, 5000);
                } else if (Rs2Widget.hasWidget("Click here to continue") || !Rs2Widget.clickWidget("I'd like a training sword and shield.")) {
                    Rs2Dialogue.clickContinue();
                }
            }

            return false;
        }

        return Rs2Walker.walkTo(goblinArea, 3);
    }
}
