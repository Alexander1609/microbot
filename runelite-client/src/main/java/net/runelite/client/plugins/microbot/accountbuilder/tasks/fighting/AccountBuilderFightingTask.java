package net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting;

import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistPlugin;
import net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript;
import net.runelite.client.plugins.microbot.playerassist.combat.BuryScatterScript;
import net.runelite.client.plugins.microbot.playerassist.combat.FlickerScript;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.playerassist.loot.LootScript;
import net.runelite.client.plugins.microbot.playerassist.skill.AttackStyleScript;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;

public abstract class AccountBuilderFightingTask extends AccountBuilderTask {
    protected final PlayerAssistConfig config;

    protected final AttackNpcScript attackNpc = new AttackNpcScript();
    private final FoodScript foodScript = new FoodScript();
    private final LootScript lootScript = new LootScript();
    private final BuryScatterScript buryScript = new BuryScatterScript();
    private final FlickerScript flickerScript = new FlickerScript();
    private final AttackStyleScript attackStyleScript = new AttackStyleScript();

    public AccountBuilderFightingTask(PlayerAssistConfig config){
        this.config = config;
    }

    @Override
    public void run() {
        super.run();

        attackNpc.run(config);
        foodScript.run(config);
        lootScript.run(config);
        buryScript.run(config);
        flickerScript.run(config);
        attackStyleScript.run(config);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        attackNpc.shutdown();
        foodScript.shutdown();
        lootScript.shutdown();
        buryScript.shutdown();
        flickerScript.shutdown();
        attackStyleScript.shutdown();
    }

    @Override
    public void onGameTick(GameTick gameTick) {
        if (PlayerAssistPlugin.getCooldown() > 0 && !Rs2Combat.inCombat())
            PlayerAssistPlugin.setCooldown(PlayerAssistPlugin.getCooldown() - 1);

        if(config.togglePrayer())
            flickerScript.onGameTick();
    }
}
