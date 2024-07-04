package net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
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

public class GiantFrogFightingTask extends AccountBuilderFightingTask {
    public GiantFrogFightingTask(){
        super(new PlayerAssistConfig() {
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
            public boolean toggleBalanceCombatSkills() {
                return true;
            }
        });

        memberOnly = false;
    }

    String food = "Shrimps";
    boolean gettingAdventurerGear = false;
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
    public void tick() {
        if (Rs2Player.isAnimating() || Rs2Player.isMoving() || Microbot.pauseAllScripts) return;

        if (!Rs2Inventory.hasItem(food)) {
            attackNpc.shutdown();
            getFoodFromBank();
        } else if (!attackNpc.isRunning())
            attackNpc.run(config);

        if (Rs2Inventory.hasItem(food) && !Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(frogsArea) > 10)
            Rs2Walker.walkTo(frogsArea, 10);

        if (!Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(frogsArea) < 10 && Rs2Player.getPlayers().stream().filter(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 20).count() > 2)
            Rs2Player.logout();
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
                        Rs2Bank.withdrawAndEquip("Amulet of strength");
                    } else {
                        Rs2Bank.withdrawAndEquip("Steel med helm");
                    }
                }
            } else {
                if (Rs2Inventory.getEmptySlots() < 28 && !gettingAdventurerGear){
                    if (!Rs2Bank.walkToBankAndUseBank())
                        return false;

                    Rs2Bank.depositAll();
                    return false;
                } else
                    gettingAdventurerGear = true;

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
                Rs2Inventory.interact("Bronze kiteshield");
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
