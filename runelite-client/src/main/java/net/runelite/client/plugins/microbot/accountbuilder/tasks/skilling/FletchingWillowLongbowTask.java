package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.fletching.FletchingConfig;
import net.runelite.client.plugins.microbot.fletching.FletchingScript;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingItem;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMaterial;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

public class FletchingWillowLongbowTask extends AccountBuilderTask {
    FletchingScript fletchingScript = new FletchingScript();

    @Override
    public String getName() {
        return "Fletching: Willow longbow";
    }

    public FletchingWillowLongbowTask(){
        skill = Skill.FLETCHING;
        minLevel = 40;
        maxLevel = 50;

        addRequirement(ItemID.KNIFE, 1);
        addRequirement(ItemID.WILLOW_LOGS, 2000);
    }

    @Override
    public void run() {
        super.run();

        fletchingScript.run(new FletchingConfig() {
            @Override
            public FletchingMode fletchingMode() {
                return FletchingMode.UNSTRUNG;
            }

            @Override
            public FletchingMaterial fletchingMaterial() {
                return FletchingMaterial.WILLOW;
            }

            @Override
            public FletchingItem fletchingItem() {
                return FletchingItem.LONG;
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

        fletchingScript.shutdown();
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || running && !fletchingScript.isRunning();
    }
}
