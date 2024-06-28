package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.helpers.quests.recruitmentdrive.DoorPuzzle;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.awt.*;
import java.util.Arrays;

public class RecruitmentDriveTask extends AccountBuilderQuestTask {
    public RecruitmentDriveTask(){
        super(QuestHelperQuest.RECRUITMENT_DRIVE);
    }

    // TODO Check other challenges (only 5/7 so far)

    @Override
    public void tick() {
        super.tick();

        if (currentStep instanceof DoorPuzzle){
            blockStuckPrevention = true;
            var puzzle = (DoorPuzzle) currentStep;

            for (var widget : puzzle.getHighlightButtons().entrySet()){
                if (widget.getValue() == 0)
                    continue;

                Rs2Widget.clickWidget(285, widget.getValue());
                return;
            }
        } else
            blockStuckPrevention = false;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == 7323 && !Rs2Player.isInteracting()) {
            Rs2GameObject.interact(7323);
            Rs2Player.waitForWalking();
        } else if (step.objectID == 7317) {
            Rs2GameObject.interact(7317);
            Rs2Player.waitForWalking();
        } else if (step.objectID == 7274) {
            Rs2GameObject.interact(7274);
            Rs2Player.waitForWalking();
        } else if (step.objectID == 7326 && Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2479, 4940, 0)) > 1) {
            Rs2GameObject.interact(new WorldPoint(2477, 4940, 0));
            Rs2Player.waitForWalking();
        } else if (step.getText().stream().anyMatch(x -> x.contains("Click the ")) && !Rs2Dialogue.hasContinue()){
            Rs2GameObject.interact(step.getWorldPoint(), "Touch");
            Rs2Player.waitForWalking();
            Rs2GameObject.interact(7302);
            Rs2Player.waitForWalking();
        } else if (step.getText().stream().anyMatch(x -> x.contains("bridge"))){
            var isEast = Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2483, 4972, 0))
                    < Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2477, 4972, 0));
            var isGOEast = step.getWorldPoint().distanceTo(new WorldPoint(2483, 4972, 0))
                    < step.getWorldPoint().distanceTo(new WorldPoint(2477, 4972, 0));

            var bridgeId = isEast ? 7286 : 7287;
            if (isEast != isGOEast){
                Rs2GameObject.interact(bridgeId);
                Rs2Player.waitForWalking(10_000);
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("bridge"))){
            var isEast = Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2483, 4972, 0))
                    < Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2477, 4972, 0));

            var bridgeId = isEast ? 7286 : 7287;
            if (Rs2Equipment.isWearing("Chicken")){
                Rs2GameObject.interact(bridgeId);
                Rs2Player.waitForWalking(10_000);
                sleep(500);
                Microbot.doInvoke(new NewMenuEntry(-1, 25362452, MenuAction.CC_OP.getId(), 1, -1, "Chicken"), new Rectangle(0, 0, 1, 1));
                sleep(500);
            } else if (Rs2Equipment.isWearing("Fox")){
                Rs2GameObject.interact(bridgeId);
                Rs2Player.waitForWalking(10_000);
                sleep(500);
                Microbot.doInvoke(new NewMenuEntry(-1, 25362450, MenuAction.CC_OP.getId(), 1, -1, "Fox"), new Rectangle(0, 0, 1, 1));
                sleep(500);
            } else if (Rs2Equipment.isWearing("Grain")){
                Rs2GameObject.interact(bridgeId);
                Rs2Player.waitForWalking(10_000);
                sleep(500);
                Microbot.doInvoke(new NewMenuEntry(-1, 25362448, MenuAction.CC_OP.getId(), 1, -1, "Grain"), new Rectangle(0, 0, 1, 1));
                sleep(500);
            }
        } else if (step.getText().stream().anyMatch(x -> x.contains("Use a vial of liquid on the tin")))
            Rs2Inventory.combine(ItemID.VIAL_OF_LIQUID, ItemID.TIN);
        else if (step.getText().stream().anyMatch(x -> x.contains("Use a vial of Gypsum on the tin")))
            Rs2Inventory.combine(ItemID.GYPSUM, ItemID.TIN);
        else if (step.getText().stream().anyMatch(x -> x.contains("Use Tin on the cupric")))
            Rs2Inventory.combine(ItemID.TIN_5594, ItemID.CUPRIC_ORE_POWDER);
        else if (step.getText().stream().anyMatch(x -> x.contains("Use Tin on the tin ore")))
            Rs2Inventory.combine(ItemID.TIN_5596, ItemID.TIN_ORE_POWDER);
        else if (step.getText().stream().anyMatch(x -> x.contains("Use your chisel,knife")))
            Rs2Inventory.combine(ItemID.KNIFE_5605, ItemID.TIN_5598);
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.SIR_LEYE){
            if (!Rs2Equipment.isWearing(ItemID.STEEL_WARHAMMER)){
                Rs2GroundItem.loot(ItemID.STEEL_WARHAMMER);
                Rs2Player.waitForWalking();
                sleep(200);
                Rs2Inventory.wear(ItemID.STEEL_WARHAMMER);
            } else if (!Rs2Combat.inCombat()){
                Rs2Npc.interact(NpcID.SIR_LEYE, "Attack");
            }
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (Arrays.stream(EquipmentInventorySlot.values()).anyMatch(Rs2Equipment::hasEquippedSlot)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositEquipment();
            return false;
        }

        return clearInventory();
    }
}
