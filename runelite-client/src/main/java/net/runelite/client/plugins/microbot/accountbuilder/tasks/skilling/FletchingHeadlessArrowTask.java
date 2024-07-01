package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.bankjs.BanksBankStander.BanksBankStanderConfig;
import net.runelite.client.plugins.microbot.bankjs.BanksBankStander.BanksBankStanderScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import java.awt.event.KeyEvent;

public class FletchingHeadlessArrowTask extends AccountBuilderTask {
    @Override
    public String getName() {
        return "Fletching: Headless arrow";
    }

    public FletchingHeadlessArrowTask(){
        skill = Skill.FLETCHING;
        maxLevel = 10;

        itemRequirements.add(new ItemRequirement("Arrow shaft", ItemID.ARROW_SHAFT, 1165));
        itemRequirements.add(new ItemRequirement("Feather", ItemID.FEATHER, 1165));
    }

    long lastAnimateTime = 0;

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet();
    }

    @Override
    public void tick() {
        if (!Rs2Inventory.contains("Feather")){
            cancel();
            return;
        }

        minTickTime = 50;
        maxTickTime = 50;

        if (Rs2Player.isAnimating())
            lastAnimateTime = System.currentTimeMillis();

        if (lastAnimateTime + 2000 < System.currentTimeMillis()){
            Rs2Inventory.combine(ItemID.ARROW_SHAFT, ItemID.FEATHER);
            sleepUntil(() -> Rs2Widget.isWidgetVisible(270, 0), 2000);
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            sleepUntil(Rs2Player::isAnimating, 2000);
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
