package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.awt.*;
import java.util.Comparator;
import java.util.stream.Collectors;

public class DeathToTheDorgeshuunTask extends AccountBuilderQuestTask {
    public DeathToTheDorgeshuunTask(){
        super(QuestHelperQuest.DEATH_TO_THE_DORGESHUUN,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Adamant kiteshield", ItemID.ADAMANT_KITESHIELD, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 8),
                new ItemRequirement("Kandarin headgear", ItemCollections.KANDARIN_HEADGEARS),
                new ItemRequirement("Bronze pick", ItemID.BRONZE_PICKAXE));

        useFood = true;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if ((step.objectID == 5492 || step.objectID == 15766) && !step.getObjects().isEmpty()) {
            if (!Rs2GameObject.canWalkTo(step.getObjects().get(0), 10))
                Rs2Walker.walkTo(step.getWorldPoint());

            stopQuest();
            Rs2GameObject.interact(step.getObjects().get(0), "Pick-Lock");
            Rs2Player.waitForAnimation();
        } else if (MQuestScript.getFullText(step).contains("Talk to Juna with both hands free. Collect 20 tears of Guthix.")){
            if (Rs2Equipment.hasEquippedSlot(EquipmentInventorySlot.WEAPON))
                Rs2Equipment.remove(EquipmentInventorySlot.WEAPON);
            else if (Rs2Equipment.hasEquippedSlot(EquipmentInventorySlot.SHIELD))
                Rs2Equipment.remove(EquipmentInventorySlot.SHIELD);

            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3254, 9512, 10, 11, 2)) == 0){
                var tearsWidget = Rs2Widget.getWidget(276, 19);
                if (Integer.parseInt(tearsWidget.getText()) >= 20){
                    startupQuest();
                    return;
                }

                stopQuest();
                blockStuckPrevention = true;

                var closestBlueTears = Rs2GameObject.getTileObjects().stream()
                        .filter(x -> x.getId() == ObjectID.BLUE_TEARS || x.getId() == ObjectID.BLUE_TEARS_6665)
                        .min(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation())))
                        .orElse(null);

                if (closestBlueTears != null && closestBlueTears.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) > 1){
                    var weepingWall = Rs2GameObject.getWallObjects(ObjectID.WEEPING_WALL, closestBlueTears.getWorldLocation()).stream().findFirst().orElse(null);
                    Rs2GameObject.interact(weepingWall, "Collect-from");
                    Rs2Player.waitForWalking();
                }
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Stand behind the guard and talk to them so they turn their back to Zanik.")
            || MQuestScript.getFullText(step).contains("Stand near to the next guard, then tell Zanik to wait there.")
                || MQuestScript.getFullText(step).contains("Run east then south to lure the guard past Zanik.")
                || MQuestScript.getFullText(step).contains("Stand in the north east, just out of sight of the last guard, and tell Zanik to wait there.")){
            Rs2Walker.walkFastCanvas(step.getWorldPoint());
            Rs2Player.waitForWalking();
        } else if (MQuestScript.getFullText(step).contains("Approach the final guard from the west so Zanik can kill them.")){
            stopQuest();
            blockStuckPrevention = true;

            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2576, 5195, 2, 6, 0)) == 0){
                Rs2Walker.walkFastCanvas(new WorldPoint(2575, 5195, 0));
                Rs2Player.waitForWalking();
            } else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2567, 5195, 9, 1, 0)) == 0){
                Rs2Walker.walkFastCanvas(new WorldPoint(2566, 5198, 0));
                Rs2Player.waitForWalking();
            } else {
                Rs2Walker.walkFastCanvas(step.getWorldPoint());
                Rs2Player.waitForWalking();
            }
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.GUARD_4517){
            stopQuest();
            blockStuckPrevention = true;

            if (Rs2Dialogue.isInDialogue()){
                Rs2Dialogue.clickContinue();
                return;
            }

            var cracks = Rs2GameObject.getTileObjects(ObjectID.CRACK_15731)
                    .stream().sorted(Comparator.comparing(x -> x.getWorldLocation().distanceTo(WorldPoint.toLocalInstance(Microbot.getClient(), new WorldPoint(2569, 5190, 0)).iterator().next())))
                    .collect(Collectors.toList());

            var middleGuardTarget = WorldPoint.toLocalInstance(Microbot.getClient(), new WorldPoint(2570, 5195, 0)).iterator().next();

            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2566, 5187, 9, 3, 0)) == 0){
                Rs2GameObject.interact(cracks.get(0));
                Rs2Player.waitForAnimation();
            } else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2567, 5190, 5, 5, 0)) == 0){
                if (!Rs2Player.getWorldLocation().equals(new WorldPoint(2569, 5194, 0)))
                    Rs2Walker.walkFastCanvas(new WorldPoint(2569, 5194, 0));

                var middleGuard = Rs2Npc.getNpc(NpcID.GUARD_4518);
                if (!Global.sleepUntilTrue(() -> middleGuard.getWorldLocation().equals(middleGuardTarget) && middleGuard.getOrientation() == 1536, 10, 5000))
                    return;
                Rs2GameObject.interact(cracks.get(1));
                Rs2Player.waitForAnimation();
                Rs2Npc.interact(NpcID.GUARD_4517);
                Rs2Player.waitForWalking();
            }
        } else if (step.npcID == NpcID.GUARD){
            if (!Rs2Equipment.isWearing(ItemID.RUNE_SCIMITAR))
                Rs2Inventory.wear(ItemID.RUNE_SCIMITAR);
            if (!Rs2Equipment.isWearing(ItemID.ADAMANT_KITESHIELD))
                Rs2Inventory.wear(ItemID.ADAMANT_KITESHIELD);
        }
    }
}
