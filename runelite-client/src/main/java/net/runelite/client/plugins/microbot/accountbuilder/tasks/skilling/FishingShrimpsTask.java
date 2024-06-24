package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.beginnerfishing.BeginnerFishingConfig;
import net.runelite.client.plugins.microbot.beginnerfishing.BeginnerFishingScript;

public class FishingShrimpsTask extends AccountBuilderTask {
    BeginnerFishingScript script = new BeginnerFishingScript();

    public FishingShrimpsTask(){
        skill = Skill.FISHING;
        maxLevel = 25;
        memberOnly = false;
    }

    @Override
    public String getName() {
        return "Fishing: Shrimps";
    }

    @Override
    public void run() {
        script.run(new BeginnerFishingConfig() {});
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        script.shutdown();
    }
}
