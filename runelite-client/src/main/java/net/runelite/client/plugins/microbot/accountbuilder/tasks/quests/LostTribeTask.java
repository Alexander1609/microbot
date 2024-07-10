package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class LostTribeTask extends AccountBuilderQuestTask {
    public LostTribeTask(){
        super(QuestHelperQuest.THE_LOST_TRIBE,
                new ItemRequirement("Kandarin headgear", ItemCollections.KANDARIN_HEADGEARS));
    }
}
