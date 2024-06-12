package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

import java.util.concurrent.TimeUnit;

public class RomeoJulietTask extends AccountBuilderTask {
    QuestStep currentStep;

    public RomeoJulietTask(){
        quest = QuestHelperQuest.ROMEO__JULIET;
    }

    @Override
    public void init() {
        super.init();

        currentStep = null;
    }

    @Override
    public void run() {
        super.run();

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            if (Microbot.isAnimating() || Microbot.isMoving() || Microbot.pauseAllScripts) return;

            var step = quest.getQuestHelper().getCurrentStep();
            if (step != null)
                currentStep = step;

            /*if (Rs2Inventory.contains(ItemID.MESSAGE) && Rs2Widget.hasWidget("Click here to continue")){
                if (isQuestRunning())
                    stopQuest();

                Rs2Dialogue.clickContinue();
                sleep(1000);
            } else if (!isQuestRunning())
                startupQuest();*/

            if (Rs2Player.getWorldLocation().equals(new WorldPoint(3158, 3427, 1))) {
                Rs2GameObject.interact(Rs2Walker.getTile(new WorldPoint(3158, 3426, 1)).getWallObject());
            }

            if (currentStep instanceof ConditionalStep){
                var conditionalStep = (ConditionalStep)currentStep;

                if (conditionalStep.getSteps().toArray()[0] instanceof ObjectStep){
                    var objectStep = (ObjectStep)conditionalStep.getSteps().toArray()[0];

                    if (objectStep.objectID == ObjectID.STAIRCASE_11797 && objectStep.isStarted()){
                        if (!Rs2Walker.walkTo(new WorldPoint(3157, 3434, 0), 3)){
                            if (isQuestRunning())
                                stopQuest();
                        }
                        else if (!isQuestRunning()){
                            startupQuest();
                        }
                    }
                    else if (!isQuestRunning())
                        startupQuest();
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Inventory.contains("Cadava berries")){
            if (!Rs2Walker.walkTo(new WorldPoint(3260, 3368, 0)))
                return false;

            Rs2GameObject.interact("Cadava bush", "Pick");
            Rs2Player.waitForAnimation();
            return false;
        }

        return true;
    }
}
