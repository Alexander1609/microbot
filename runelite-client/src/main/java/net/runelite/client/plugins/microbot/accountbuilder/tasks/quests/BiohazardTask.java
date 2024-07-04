package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class BiohazardTask extends AccountBuilderQuestTask {
    public BiohazardTask(){
        super(QuestHelperQuest.BIOHAZARD);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20
                && isQuestCompleted(QuestHelperQuest.TREE_GNOME_VILLAGE);
    }

    @Override
    public boolean doTaskPreparations() {
        // TODO gas mask might be equipped
        return clearInventory() && withdrawBuyItems();
    }
}
