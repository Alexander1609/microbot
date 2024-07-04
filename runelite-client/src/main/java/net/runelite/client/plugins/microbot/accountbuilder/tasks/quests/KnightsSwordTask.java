package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class KnightsSwordTask extends AccountBuilderQuestTask {
    public KnightsSwordTask(){
        super(QuestHelperQuest.THE_KNIGHTS_SWORD,
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10));
        useFood = true;
    }

    // TODO Handle Sir Vyvin looking at you, maybe look at being attacked while mining


    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 30;
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
