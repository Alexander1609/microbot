package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.crafting.CraftingConfig;
import net.runelite.client.plugins.microbot.crafting.enums.Activities;
import net.runelite.client.plugins.microbot.crafting.enums.Glass;
import net.runelite.client.plugins.microbot.crafting.scripts.GlassblowingScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

public class CraftingGlassblowingTask extends AccountBuilderTask {
    GlassblowingScript glassblowingScript = new GlassblowingScript();

    @Override
    public String getName() {
        return "Crafting: Glassblowing";
    }

    public CraftingGlassblowingTask(){
        skill = Skill.CRAFTING;
        minLevel = 33;
        maxLevel = 70;

        addRequirement(ItemID.GLASSBLOWING_PIPE, 1);
        addRequirement(ItemID.MOLTEN_GLASS, 2000);
    }

    @Override
    public void run() {
        super.run();

        glassblowingScript.run(new CraftingConfig() {
            @Override
            public Activities activityType() {
                return Activities.GLASSBLOWING;
            }

            @Override
            public Glass glassType() {
                return Microbot.getClient().getRealSkillLevel(Skill.CRAFTING) < 46 ? Glass.PROGRESSIVE : Glass.UNPOWERED_ORB;
            }

            @Override
            public boolean Afk() {
                return true;
            }
        });
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        glassblowingScript.shutdown();
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || running && !Rs2Bank.hasBankItem(ItemID.MOLTEN_GLASS, 1) && !Rs2Inventory.contains(ItemID.MOLTEN_GLASS);
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
