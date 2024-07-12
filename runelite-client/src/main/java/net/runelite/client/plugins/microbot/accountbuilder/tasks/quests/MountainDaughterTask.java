package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class MountainDaughterTask extends AccountBuilderQuestTask {
    public MountainDaughterTask(){
        super(QuestHelperQuest.MOUNTAIN_DAUGHTER,
                new ItemRequirement("Ardy cloak", ItemCollections.ARDY_CLOAKS, 1, true),
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Adamant kiteshield", ItemID.ADAMANT_KITESHIELD, 1, true),
                new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF, 1),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10),
                new ItemRequirement("Bronze pick", ItemID.BRONZE_PICKAXE, 1),
                new ItemRequirement("Plank", ItemID.PLANK, 1),
                new ItemRequirement("Leather gloves", ItemID.LEATHER_GLOVES, 1, true));

        useFood = true;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.CLUMP_OF_ROCKS){
            var req = (ItemRequirement)step.getRequirements().stream().filter(x -> x.getDisplayText().toLowerCase().contains("pole"))
                    .findFirst().get();
            var pole = req.getAllIds().stream().filter(Rs2Inventory::contains).findFirst().get();

            Rs2Inventory.useItemOnObject(pole, ObjectID.CLUMP_OF_ROCKS);
            Rs2Player.waitForAnimation();
        } else if (step.objectID == ObjectID.THORNY_BUSHES){
            Rs2GameObject.interact(ObjectID.THORNY_BUSHES, "pick-from");
            Rs2Inventory.waitForInventoryChanges();
        } else if (step.objectID == ObjectID.CAVE_ENTRANCE_5857){
            var pos = new WorldPoint(2801, 3703, 0);
            var go = Rs2GameObject.getGameObject(new WorldPoint(2802, 3703, 0));
            if (go == null || Rs2Player.getWorldLocation().distanceTo(step.getWorldPoint()) < 7) {
                pos = new WorldPoint(2801, 3703, 0);
                go = Rs2GameObject.getGameObject(new WorldPoint(2807, 3703, 0));
            }

            if (go != null) {
                Rs2Walker.walkFastCanvas(pos);
                Rs2Player.waitForWalking();
                Rs2GameObject.interact(go, "Chop down");
                Rs2Player.waitForAnimation();
            }
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.THE_KENDAL){
            if (!Rs2Dialogue.isInDialogue() && !Rs2Combat.inCombat())
                Rs2Npc.interact(NpcID.THE_KENDAL, "Talk-to");
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Pick up the Corpse of Woman.")) {
            Rs2GroundItem.loot(ItemID.CORPSE_OF_WOMAN);
            Rs2Inventory.waitForInventoryChanges();
        } else if (MQuestScript.getFullText(step).contains("Return to the centre of the lake and bury the corpse.")) {
            Rs2Walker.walkFastCanvas(step.getWorldPoint());
            Rs2Player.waitForWalking();
            Rs2Inventory.interact(ItemID.CORPSE_OF_WOMAN, "Bury");
            Rs2Inventory.waitForInventoryChanges();
        }
    }
}
