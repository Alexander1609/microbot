package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

public class RomeoJulietTask extends AccountBuilderQuestTask {
    QuestStep currentStep;

    public RomeoJulietTask(){
        super(QuestHelperQuest.ROMEO__JULIET);
        memberOnly = false;
    }

    @Override
    public void init() {
        super.init();

        currentStep = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Player.getWorldLocation().equals(new WorldPoint(3158, 3427, 1))) {
            Rs2GameObject.interact(Rs2Walker.getTile(new WorldPoint(3158, 3426, 1)).getWallObject());
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.STAIRCASE_11797){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3156, 3433, 5, 3, 0)) != 0){
                if (isQuestRunning())
                    stopQuest();

                Rs2Walker.walkTo(new WorldPoint(3157, 3434, 0), 1);
            }
            else if (!isQuestRunning()){
                startupQuest();
            }
        }
        else if (!isQuestRunning())
            startupQuest();
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
