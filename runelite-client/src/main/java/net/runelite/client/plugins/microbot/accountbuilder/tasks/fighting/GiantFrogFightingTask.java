package net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript;
import net.runelite.client.plugins.microbot.playerassist.combat.BuryScatterScript;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.playerassist.loot.LootScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GiantFrogFightingTask extends AccountBuilderTask {
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
            return "Giant frog";
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
        public WorldPoint centerLocation() {
            return new WorldPoint(3197, 3182, 0);
        }

        @Override
        public int attackRadius() {
            return 100;
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
    boolean hasAdventurerGear = true;
    WorldArea frogsArea = new WorldArea(3195, 3182, 5, 5, 0);

    @Override
    public String getName() {
        return "Fighting: Giant frogs";
    }

    @Override
    public boolean requirementsMet() {
        return Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 20
                && Microbot.getClient().getRealSkillLevel(Skill.STRENGTH) >= 20
                && Microbot.getClient().getRealSkillLevel(Skill.DEFENCE) >= 20
                && (Microbot.getClient().getRealSkillLevel(Skill.ATTACK) < 40
                    || Microbot.getClient().getRealSkillLevel(Skill.STRENGTH) < 40
                    || Microbot.getClient().getRealSkillLevel(Skill.DEFENCE) < 40);
    }

    @Override
    public void run() {
        attackNpc.run(config);
        foodScript.run(config);
        lootScript.run(config);
        buryScript.run(config);

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            try {
                if (Rs2Player.isAnimating() || Rs2Player.isMoving() || Microbot.pauseAllScripts) return;

                if (!Rs2Inventory.hasItem(food)) {
                    attackNpc.shutdown();
                    getFoodFromBank();
                } else if (!attackNpc.isRunning())
                    attackNpc.run(config);

                if (Rs2Inventory.hasItem(food) && !Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(frogsArea) > 10)
                    Rs2Walker.walkTo(frogsArea, 5);

                if (!Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(frogsArea) < 10 && Rs2Player.getPlayers().stream().filter(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 20).count() > 2)
                    Rs2Player.logout();

                if (nextCombatSwap < System.currentTimeMillis()) {
                    nextCombatSwap = System.currentTimeMillis() + 240_000 + new Random().nextInt(360_000);

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
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace(System.out);
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
        if (!Rs2Equipment.isWearing("Mithril longsword")){
            if (hasAdventurerGear){
                if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                    return false;

                Rs2Bank.depositEquipment();

                if (!Rs2Bank.hasBankItem("Mithril longsword"))
                    hasAdventurerGear = false;
                else {
                    Rs2Bank.withdrawAndEquip("Mithril longsword");
                    Rs2Bank.withdrawAndEquip("Mithril chainbody");
                    Rs2Bank.withdrawAndEquip("Leather boots");
                    Rs2Bank.withdrawAndEquip("Leather gloves");

                    if (Rs2Bank.hasItem("Mithril full helm")){
                        Rs2Bank.withdrawAndEquip("Mithril full helm");
                        Rs2Bank.withdrawAndEquip("Mithril kiteshield");
                        Rs2Bank.withdrawAndEquip("Mithril platelegs");
                    } else {
                        Rs2Bank.withdrawAndEquip("Steel med helm");
                    }
                }
            } else {
                if (!Rs2Inventory.hasItem("Mithril longsword") || Rs2Dialogue.isInDialogue()){
                    if (!Rs2Walker.walkTo(new WorldPoint(3232, 3233, 0), 2))
                        return false;

                    if (!Rs2Dialogue.isInDialogue()) {
                        Rs2Npc.interact(NpcID.ADVENTURER_JON_9244, "Claim");
                        sleepUntil(Rs2Dialogue::isInDialogue, 5_000);
                    }
                    else if (Rs2Dialogue.hasSelectAnOption())
                        Rs2Walker.walkFastCanvas(Rs2Player.getWorldLocation());
                    else
                        Rs2Dialogue.clickContinue();
                } else {
                    if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                        return false;

                    Rs2Bank.depositAll();
                    hasAdventurerGear = true;
                }
            }

            return false;
        }

        if (!Rs2Equipment.hasEquippedSlot(EquipmentInventorySlot.SHIELD) && Microbot.getClient().getRealSkillLevel(Skill.SMITHING) >= 12){
            if (Rs2Bank.hasItem("Bronze kiteshield")){
                if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                    return false;

                Rs2Bank.withdrawAndEquip("Bronze kiteshield");
                return false;
            }

            if (!Rs2Bank.hasItem("Hammer") && !Rs2Inventory.hasItem("Hammer")){
                cancel();
                return false;
            }

            if (Rs2Inventory.hasItem("Bronze kiteshield"))
                Rs2Inventory.wield("Bronze kiteshield");
            else if (!Rs2Inventory.hasItem("Bronze bar")){
                if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                    return false;

                Rs2Bank.withdrawX("Bronze bar", 3);
                Rs2Bank.withdrawItem("Hammer");
            } else if (!Rs2Player.isAnimating()) {
                if (!Rs2Walker.walkTo(new WorldPoint(3225, 3250, 0), 2))
                    return false;

                if (!Rs2Widget.isWidgetVisible(312, 0))
                    Rs2GameObject.interact("Rusted anvil");
                else
                    Rs2Widget.clickWidget("Kite shield");
            }

            return false;
        }

        if (Rs2Inventory.count(food) < 10 && !getFoodFromBank())
            return false;

        return Rs2Walker.walkTo(new WorldArea(3195, 3182, 5, 5, 0), 3);
    }
}
