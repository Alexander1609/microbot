package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class TheQueenOfThievesTask extends AccountBuilderQuestTask {
    public TheQueenOfThievesTask(){
        super(QuestHelperQuest.THE_QUEEN_OF_THIEVES,
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true));
    }
}
