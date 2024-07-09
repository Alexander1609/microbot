package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class RFDGobilGeneralsTask extends AccountBuilderQuestTask {
    public RFDGobilGeneralsTask(){
        super(QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE);
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
