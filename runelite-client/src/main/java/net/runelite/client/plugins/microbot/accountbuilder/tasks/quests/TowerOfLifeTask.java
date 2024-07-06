package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class TowerOfLifeTask extends AccountBuilderQuestTask {
    public TowerOfLifeTask(){
        super(QuestHelperQuest.TOWER_OF_LIFE,
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true));
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
