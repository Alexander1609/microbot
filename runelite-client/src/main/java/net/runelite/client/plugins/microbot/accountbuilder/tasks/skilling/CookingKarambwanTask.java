package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

import java.awt.event.KeyEvent;

public class CookingKarambwanTask extends AccountBuilderTask {
    @Override
    public String getName() {
        return "Cooking: Karambwan";
    }

    public CookingKarambwanTask(){
        skill = Skill.COOKING;
        minLevel = 30;
        maxLevel = 99;

        addRequirement(ItemID.RAW_KARAMBWAN, 2000);
    }

    @Override
    public void run() {
        super.run();

        blockStuckPrevention = true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && isQuestCompleted(QuestHelperQuest.TAI_BWO_WANNAI_TRIO);
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || running && !Rs2Inventory.contains(ItemID.RAW_KARAMBWAN) && !Rs2Bank.hasBankItem(ItemID.RAW_KARAMBWAN, 1);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        blockStuckPrevention = false;

        super.doTaskCleanup(shutdown);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        return Rs2Walker.walkTo(new WorldPoint(3043, 4972, 1)) && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3036, 4966, 12, 10, 1)) == 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (!Rs2Inventory.contains(ItemID.RAW_KARAMBWAN)){
            if (!Rs2Bank.isOpen()){
                if (Random.random(1, 100) < 10)
                    sleep(1000, 60000);

                var bank = Rs2Npc.getNpc(NpcID.EMERALD_BENEDICT);
                if (bank == null) {
                    Rs2Walker.walkTo(3043, 4972, 1);
                    return;
                } else {
                    Rs2Npc.interact(bank, "Bank");
                    sleepUntil(Rs2Bank::isOpen, 5000);
                    return;
                }
            }

            if (Rs2Inventory.isEmpty())
                Rs2Bank.withdrawAll(ItemID.RAW_KARAMBWAN);
            else
                Rs2Bank.depositAll();

            return;
        }

        if (Rs2Widget.isHidden(17694734))
        {
            Rs2GameObject.interact(ObjectID.FIRE_43475);
            sleepUntil(() -> !Rs2Widget.isHidden(17694734), 5000);
            sleep(600, 1600);
        } else {
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            Rs2Player.waitForAnimation();

            do {
                sleepUntil(() -> !Rs2Player.isAnimating(), 2000);
            } while (Global.sleepUntilTrue(Rs2Player::isAnimating, 10, 2000));
        }
    }
}
