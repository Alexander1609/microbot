package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class DruidicRitualTask extends AccountBuilderQuestTask {
    public DruidicRitualTask(){
        super(QuestHelperQuest.DRUIDIC_RITUAL);
    }

    @Override
    public void run() {
        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(
                new Restriction(2888, 9830, 0),
                new Restriction(2888, 9831, 0),
                new Restriction(2892, 9825, 0),
                new Restriction(2893, 9825, 0)
        );

        super.run();
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 30;
    }
}
