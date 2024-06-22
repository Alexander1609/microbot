package net.runelite.client.plugins.microbot.beginnerfishing;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Spells;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;


public class BeginnerFishingScript extends Script {
    public static double version = 1.0;
    public String[] items = { "Small fishing net", "Tinderbox" };
    public String[] rawFish = { "Raw shrimps", "Raw anchovies" };

    public boolean run(BeginnerFishingConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                if (!Arrays.stream(items).allMatch(Rs2Inventory::contains)){
                    Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_TOP);
                    Rs2Bank.useBank();
                    Rs2Bank.depositEquipment();
                    Rs2Bank.depositAll(x -> Arrays.stream(items).noneMatch(y -> x.name.equals(y)));

                    for (var item : items)
                        if (!Rs2Inventory.contains(item))
                            Rs2Bank.withdrawOne(item);

                    return;
                }

                if (Rs2Inventory.isFull() || Rs2Inventory.get(x -> !Arrays.asList(ArrayUtils.addAll(items, rawFish)).contains(x.name)) != null){
                    if (Rs2Inventory.get(rawFish) != null){
                        var logsArea = new WorldArea(3205, 3224, 2, 2, 2);
                        if (logsArea.distanceTo(Rs2Player.getWorldLocation()) > 10){
                            var points = logsArea.toWorldPointList();
                            var index = new Random().nextInt(points.size());
                            Rs2Walker.walkTo(points.get(index));
                        }

                        var fire = Rs2GameObject.findObject("fire", true, 100, true, Rs2Player.getWorldLocation());

                        if (fire == null){
                            var logs = Arrays.stream(Rs2GroundItem.getAll(10))
                                    .filter(x -> x.getItem().getName().equals("Logs"))
                                    .filter(x -> Rs2GameObject.getGameObject(x.getTile().getWorldLocation()) == null)
                                    .findFirst();

                            if (logs.isEmpty())
                                return;

                            Rs2Inventory.use("tinderbox");
                            sleep(100, 300);
                            Rs2GroundItem.interact(logs.get());
                            return;
                        }

                        var item = Rs2Inventory.get(rawFish).name;
                        Rs2Inventory.use(item);
                        Rs2GameObject.interact("fire");

                        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694734) != null);
                        sleep(600, 1600);
                        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                        sleep(5000);
                        while (true) {
                            var rawFoodCount = Rs2Inventory.get(item).quantity;
                            sleep(3000);
                            if (rawFoodCount == Rs2Inventory.get(item).quantity)
                                break;
                        }
                    } else {
                        Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_TOP);
                        Rs2Bank.useBank();
                        Rs2Bank.depositAll(x -> Arrays.stream(items).noneMatch(y -> x.name.contains(y)));
                    }
                }
                else {
                    var shrimpArea = new WorldArea(3241, 3153, 3, 3, 0);
                    if (shrimpArea.distanceTo(Rs2Player.getWorldLocation()) > 20){
                        var points = shrimpArea.toWorldPointList();
                        var index = new Random().nextInt(points.size());
                        Rs2Walker.walkTo(points.get(index));

                        return;
                    }

                    for (var spot : FishingSpot.SHRIMP.getIds()){
                        var npc = Rs2Npc.getNpc(spot);
                        if (npc != null && !Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
                            validateInteractable(npc);
                        }
                        Rs2Npc.interact(npc, "net");
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
