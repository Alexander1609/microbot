package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class DeathPlateauTask extends AccountBuilderQuestTask {
    public DeathPlateauTask(){
        super(QuestHelperQuest.DEATH_PLATEAU,
                new ItemRequirement("Blurberry special", ItemID.BLURBERRY_SPECIAL));
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.HAROLD && step.getText().stream().anyMatch(x -> x.contains("Gamble"))){
            if(isQuestRunning()) {
                stopQuest();
                blockStuckPrevention = true;
            }

            if (Rs2Inventory.contains(ItemID.IOU)){
                startupQuest();
                blockStuckPrevention = false;
            } else if (Rs2Widget.isWidgetVisible(99, 0)){
                if (Rs2Widget.isWidgetVisible(99, 28))
                    Rs2Widget.clickWidget(99, 28);
                else if (Rs2Widget.isWidgetVisible(99, 30))
                    Rs2Widget.clickWidget(99, 30);
            } else if (Rs2Widget.hasWidget("Enter amount:")){
                Rs2Keyboard.typeString("101");
                Rs2Keyboard.enter();
            } else if (!Rs2Dialogue.isInDialogue())
                Rs2Npc.interact(NpcID.HAROLD);
            else if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();
            else if (Rs2Dialogue.hasSelectAnOption())
                Rs2Widget.clickWidget("Would you like to gamble?");

            sleep(500);
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("mechanism"))){
            var ball = (ItemRequirement)step.getRequirements().get(0);

            if (!Rs2Inventory.contains(ball.getId()) && Rs2GroundItem.loot(ball.getId()))
                Rs2Inventory.waitForInventoryChanges();
            else {
                Rs2Inventory.use(ball.getId());
                Rs2Inventory.waitForInventoryChanges();
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Exit Tenzing's house through the north door, and go north"))){
            Rs2Walker.walkFastCanvas(new WorldPoint(2865, 3609, 0));
            Rs2Player.waitForWalking();
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet();
    }
}
