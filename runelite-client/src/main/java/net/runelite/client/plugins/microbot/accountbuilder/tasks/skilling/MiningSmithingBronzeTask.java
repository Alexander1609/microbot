package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.beginnerminer.BeginnerMinerConfig;
import net.runelite.client.plugins.microbot.beginnerminer.BeginnerMinerScript;

public class MiningSmithingBronzeTask extends AccountBuilderTask {
    BeginnerMinerScript script = new BeginnerMinerScript();

    public MiningSmithingBronzeTask(){
        skill = Skill.SMITHING;
        maxLevel = 15;
        memberOnly = false;
    }

    @Override
    public String getName() {
        return "Mining / Smithing: Bronze";
    }

    @Override
    public void run() {
        script.run(new BeginnerMinerConfig() {});
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        script.shutdown();
    }
}
