package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class KnightsSwordTask extends AccountBuilderQuestTask {
    public KnightsSwordTask(){
        super(QuestHelperQuest.THE_KNIGHTS_SWORD);
    }

    // TODO Handle Sir Vyvin looking at you, maybe look at being attacked while mining


    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 30;
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyRequiredItems();
    }
}
