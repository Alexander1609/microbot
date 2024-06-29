package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class MonksFriendTask extends AccountBuilderQuestTask {
    public MonksFriendTask(){
        super(QuestHelperQuest.MONKS_FRIEND);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20
                && isQuestCompleted(QuestHelperQuest.TREE_GNOME_VILLAGE);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("Yes, I'd be happy to!"))
            Rs2Widget.clickWidget("Yes, I'd be happy to!");
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyRequiredItems();
    }
}
