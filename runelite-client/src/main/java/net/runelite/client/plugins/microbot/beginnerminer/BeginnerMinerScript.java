package net.runelite.client.plugins.microbot.beginnerminer;

import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.beginnerminer.enums.OresEnum;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class BeginnerMinerScript extends Script {
    public static double version = 1.0;
    private static WorldArea tinCopperArea = new WorldArea(3223, 3146, 7, 4, 0);
    private static WorldArea furnaceArea = new WorldArea(3224, 3252, 6, 3, 0);

    public boolean run(BeginnerMinerConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (Microbot.isAnimating() || Microbot.isMoving() || Microbot.pauseAllScripts) return;

                if (!Rs2Equipment.isWearing("Bronze pickaxe")
                        || Rs2Inventory.get(x -> !x.name.equals(OresEnum.TIN.getOreName())
                            && !x.name.equals(OresEnum.COPPER.getOreName())
                            && !x.name.equals("Bronze bar")) != null){
                    Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_CASTLE_TOP);
                    Rs2Bank.useBank();
                    Rs2Bank.depositAll();

                    if (!Rs2Equipment.isWearing("Bronze pickaxe"))
                        Rs2Bank.withdrawAndEquip("Bronze pickaxe");

                    return;
                }

                if (!Rs2Inventory.hasItem(OresEnum.COPPER.getOreName()) && !Rs2Inventory.hasItem(OresEnum.TIN.getOreName()) && !Rs2Inventory.isEmpty()){
                    Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_CASTLE_TOP);
                    Rs2Bank.useBank();
                    Rs2Bank.depositAll();
                } else if (Rs2Inventory.isFull() || Rs2Inventory.contains("Bronze bar")){
                    Rs2Walker.walkTo(furnaceArea, 2);

                    Rs2GameObject.interact("Furnace", "Smelt", true);
                    sleepUntil(() -> Rs2Widget.getWidget(17694734) != null);
                    if (Rs2Widget.getWidget(17694734) != null)
                        Rs2Widget.clickWidget(17694734);

                    sleep(5000);
                    while (true) {
                        var bronzeBarCount = Rs2Inventory.get("Bronze bar").quantity;
                        sleep(3000);
                        if (bronzeBarCount == Rs2Inventory.get("Bronze bar").quantity)
                            break;
                    }
                } else {
                    Rs2Walker.walkTo(tinCopperArea, 20);

                    var ores = new ArrayList<>(Arrays.asList(OresEnum.COPPER, OresEnum.TIN));
                    ores.removeIf(ore -> Rs2Inventory.count(ore.getOreName()) == 14);

                    var rock = Rs2GameObject.findClosestObjects(ores.get(0).getRockName(), true, 100, Rs2Player.getWorldLocation()).get(0);
                    Rs2GameObject.interact(rock, "mine");

                    Rs2Player.waitForAnimation(5000);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
