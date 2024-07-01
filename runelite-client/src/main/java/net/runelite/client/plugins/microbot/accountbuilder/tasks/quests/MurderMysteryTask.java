package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class MurderMysteryTask extends AccountBuilderQuestTask {
    public MurderMysteryTask(){
        super(QuestHelperQuest.MURDER_MYSTERY);
    }

    @Override
    public void run() {
        super.run();

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(
                new Restriction(2747, 3577, 0),
                new Restriction(2749, 3578, 0),
                new Restriction(2750, 3578, 0)
        );
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (Rs2Widget.hasWidget("Why'd you buy poison the other day?"))
            Rs2Widget.clickWidget("Why'd you buy poison the other day?");
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
