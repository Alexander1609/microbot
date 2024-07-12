package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
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

public class SeaSlugTask extends AccountBuilderQuestTask {
    public SeaSlugTask(){
        super(QuestHelperQuest.SEA_SLUG,
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS));
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.KENNITH_5063){
            if (!Rs2Walker.walkTo(2765, 3286, 1))
                return;

            if (!Rs2Dialogue.isInDialogue()) {
                Rs2Npc.interact(NpcID.KENNITH_5063, "Talk-to");
                Rs2Player.waitForWalking();
            }
        } else if (step.npcID == NpcID.HOLGART_5072){
            Rs2Npc.interact(NpcID.HOLGART_5069, "Travel");
            Rs2Player.waitForWalking();
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Rub the dry sticks to light the unlit torch.")){
            Rs2Inventory.interact(ItemID.DRY_STICKS, "Rub-together");
            Rs2Inventory.waitForInventoryChanges();
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.CRANE_18327){
            Rs2Walker.walkFastCanvas(new WorldPoint(2772, 3291, 1));
            Rs2Player.waitForWalking();
        }
    }
}
