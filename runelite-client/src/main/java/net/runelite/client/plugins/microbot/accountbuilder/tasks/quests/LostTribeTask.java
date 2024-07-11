package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class LostTribeTask extends AccountBuilderQuestTask {
    public LostTribeTask(){
        super(QuestHelperQuest.THE_LOST_TRIBE,
                new ItemRequirement("Kandarin headgear", ItemCollections.KANDARIN_HEADGEARS),
                new ItemRequirement("Bronze pick", ItemID.BRONZE_PICKAXE));
    }

    @Override
    public void tick() {
        super.tick();

        if (currentStep != null && MQuestScript.getFullText(currentStep).contains(", and give him the treaty.")){
            if (!Rs2Inventory.contains(ItemID.PEACE_TREATY)){
                stopQuest();

                if (!Rs2Walker.walkTo(new WorldPoint(3210, 3222, 1)))
                    return;

                if (!Rs2Dialogue.isInDialogue())
                    Rs2Npc.interact(NpcID.DUKE_HORACIO);
                else if (Rs2Dialogue.hasContinue())
                    Rs2Dialogue.clickContinue();
                else if (Rs2Dialogue.hasSelectAnOption())
                    Rs2Widget.clickWidget("What was I doing again?");
            } else
                startupQuest();
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == 5492){
            stopQuest();

            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                return;

            Rs2GameObject.interact(5492, "Pick-Lock");
            Rs2Player.waitForAnimation();
        }
    }
}
