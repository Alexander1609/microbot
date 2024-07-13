package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class DemonSlayerTask extends AccountBuilderQuestTask {
    public DemonSlayerTask(){
        super(QuestHelperQuest.DEMON_SLAYER);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 45;
    }
}
