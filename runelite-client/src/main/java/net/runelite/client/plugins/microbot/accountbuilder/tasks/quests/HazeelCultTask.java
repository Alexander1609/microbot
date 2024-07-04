package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class HazeelCultTask extends AccountBuilderQuestTask {
    public HazeelCultTask(){
        super(QuestHelperQuest.HAZEEL_CULT, false);
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.LADDER_16683
                && Rs2Player.getWorldLocation().distanceTo(new WorldArea(2572, 3267, 4, 4, 1)) == 0
                && !Rs2Dialogue.isInDialogue()){
            Rs2GameObject.interact(46902);
            sleepUntil(Rs2Dialogue::isInDialogue, 2000);
        } else if (step.objectID == 46705 && Rs2Player.getWorldLocation().equals(new WorldPoint(2572, 3271, 1))){
            Rs2GameObject.interact(46902);
            sleepUntil(Rs2Dialogue::isInDialogue, 2000);
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && isQuestCompleted(QuestHelperQuest.TREE_GNOME_VILLAGE);
    }
}
