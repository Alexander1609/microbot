package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class FairytaleIIUnlockTask extends AccountBuilderQuestTask {
    public FairytaleIIUnlockTask(){
        super(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN);
    }

    @Override
    public boolean requirementsMet() {
        return isQuestCompleted(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS)
                && Microbot.getClientThread().runOnClientThread(() -> QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN.getQuestHelper().getState(Microbot.getClient()) != QuestState.IN_PROGRESS);
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == NullObjectID.NULL_29560 || step.objectID == NullObjectID.NULL_29495)
            cancel();
    }

    @Override
    public boolean doTaskPreparations() { return true; }
}
