package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class BoneVoyageTask extends AccountBuilderQuestTask {
    public BoneVoyageTask(){
        super(QuestHelperQuest.BONE_VOYAGE,
                new ItemRequirement("Comsmic", ItemID.COSMIC_RUNE, 1),
                new ItemRequirement("Fire", ItemID.FIRE_RUNE, 5),
                new ItemRequirement("Ruby neck", ItemID.RUBY_NECKLACE, 1));
    }

    @Override
    public boolean doTaskPreparations() {
        if (!super.doTaskPreparations())
            return false;

        return true;
    }
}
