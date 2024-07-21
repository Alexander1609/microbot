package net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting;

import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

public class GoblinFightingTask extends AccountBuilderFightingTask {
    public GoblinFightingTask(){
        super(new PlayerAssistConfig() {
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
                return 400;
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

            @Override
            public boolean toggleBalanceCombatSkills() {
                return true;
            }

            @Override
            public boolean toggleAvoidControlled() {
                return false;
            }
        });

        memberOnly = false;
    }

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
    public void tick() {
        if (Rs2Player.isAnimating() || Rs2Player.isMoving() || Microbot.pauseAllScripts) return;

        if (!Rs2Inventory.hasItem(food)){
            attackNpc.shutdown();
            getFoodFromBank();
        }
        else if (!attackNpc.isRunning())
            attackNpc.run(config);

        if (Rs2Inventory.hasItem(food) && !Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(goblinArea) > 10)
            Rs2Walker.walkTo(goblinArea, 5);
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

                if (Rs2Inventory.contains("Training shield")){
                    Rs2Inventory.equip("Training shield");

                    return false;
                }

                if (Rs2Inventory.contains("Training sword")){
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
