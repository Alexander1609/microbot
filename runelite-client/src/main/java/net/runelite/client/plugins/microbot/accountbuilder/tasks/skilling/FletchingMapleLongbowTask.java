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
import net.runelite.client.plugins.microbot.util.math.Random;

public class FletchingMapleLongbowTask extends AccountBuilderTask {
    FletchingScript fletchingScript = new FletchingScript();

    @Override
    public String getName() {
        return "Fletching: Maple longbow";
    }

    public FletchingMapleLongbowTask(){
        skill = Skill.FLETCHING;
        minLevel = 55;
        maxLevel = 70;

        addRequirement(ItemID.KNIFE, 1);
        addRequirement(ItemID.MAPLE_LOGS, 1000);
        addRequirement(ItemID.BOW_STRING, 1000);
    }

    FletchingMode currentMode = FletchingMode.UNSTRUNG;
    int nextSwap = Random.random(80, 150);

    @Override
    public void run() {
        super.run();

        fletchingScript.run(new FletchingConfig() {
            @Override
            public FletchingMode fletchingMode() {
                if (Rs2Bank.hasBankItem(ItemID.MAPLE_LONGBOW_U, nextSwap)){
                    currentMode = FletchingMode.STRUNG;
                    nextSwap = Random.random(80, 150);
                } else if (!Rs2Bank.hasBankItem(ItemID.MAPLE_LONGBOW_U, 1) && !Rs2Inventory.contains(ItemID.MAPLE_LONGBOW_U)){
                    currentMode = FletchingMode.UNSTRUNG;
                }

                return currentMode;
            }

            @Override
            public FletchingMaterial fletchingMaterial() {
                return FletchingMaterial.MAPLE;
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