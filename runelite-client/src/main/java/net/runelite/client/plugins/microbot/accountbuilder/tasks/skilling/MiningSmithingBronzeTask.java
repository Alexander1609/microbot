package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.ArrayList;
import java.util.Arrays;

public class MiningSmithingBronzeTask extends AccountBuilderTask {
    public MiningSmithingBronzeTask(){
        skill = Skill.SMITHING;
        maxLevel = 15;
        memberOnly = false;

        minTickTime = 50;
        maxTickTime = 50;
    }

    @Override
    public String getName() {
        return "Mining / Smithing: Bronze";
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Equipment.isWearing("Bronze pickaxe")){
            if (!Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_TOP) || !Rs2Bank.openBank())
                return false;

            if (!Rs2Equipment.isWearing("Bronze pickaxe"))
                Rs2Bank.withdrawAndEquip("Bronze pickaxe");

            Rs2Bank.depositAll();

            return false;
        }

        return true;
    }

    private static final WorldArea tinCopperArea = new WorldArea(3223, 3146, 7, 4, 0);
    private static final WorldArea furnaceArea = new WorldArea(3224, 3252, 6, 3, 0);

    private long lastAction = 0;
    private long nextWait = 500;

    @Override
    public void tick() {
        if (Rs2Player.isAnimating() || Rs2Player.isInteracting() || Rs2Player.isWalking()){
            lastAction = System.currentTimeMillis();
            return;
        }

        if (lastAction + nextWait >= System.currentTimeMillis())
            return;
        else
            nextWait = Random.random(500, 1000);

        if (Math.max(Rs2Inventory.count(OresEnum.COPPER.getOreName()), Rs2Inventory.count(OresEnum.TIN.getOreName())) == 0 && Rs2Inventory.contains(ItemID.BRONZE_BAR)){
            if (!Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_TOP) || !Rs2Bank.openBank())
                return;

            Rs2Bank.depositAll();
        } else if (Rs2Inventory.isFull() || Rs2Inventory.contains("Bronze bar")){
            Rs2Walker.walkTo(furnaceArea, 2);

            Rs2GameObject.interact("Furnace", "Smelt", true);
            sleepUntil(() -> Rs2Widget.getWidget(17694734) != null, 2000);
            sleep(500, 1000);
            if (Rs2Widget.getWidget(17694734) != null)
                Rs2Widget.clickWidget(17694734);
            Rs2Player.waitForAnimation();
        } else {
            if (!Rs2Walker.walkTo(tinCopperArea, 20))
                return;

            var ores = new ArrayList<>(Arrays.asList(OresEnum.COPPER, OresEnum.TIN));
            ores.removeIf(ore -> Rs2Inventory.count(ore.getOreName()) == 14);

            var rock = Rs2GameObject.findClosestObjects(ores.get(0).getRockName(), true, 100, Rs2Player.getWorldLocation()).get(0);
            Rs2GameObject.interact(rock, "mine");
            Rs2Player.waitForAnimation(5000);
        }
    }

    @Getter
    enum OresEnum {
        COPPER("Copper rocks", "Copper ore"),
        TIN("Tin rocks", "Tin ore");

        private final String rockName;
        private final String oreName;

        OresEnum(String rockName, String oreName) {
            this.rockName = rockName;
            this.oreName = oreName;
        }
    }
}
