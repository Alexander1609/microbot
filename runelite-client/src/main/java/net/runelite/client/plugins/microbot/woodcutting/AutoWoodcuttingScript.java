package net.runelite.client.plugins.microbot.woodcutting;

import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.grounditems.GroundItem;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class AutoWoodcuttingScript extends Script {

    public static double version = 1.5;

    public boolean run(AutoWoodcuttingConfig config) {
        if (config.hopWhenPlayerDetected()) {
            Microbot.showMessage("Make sure autologin plugin is enabled and randomWorld checkbox is checked!");
        }
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (config.burnLogs() && !Rs2Inventory.hasItem("tinderbox")){
                    var bankOpen = Rs2Bank.walkToBank() && Rs2Bank.useBank();

                    if (!bankOpen)
                        return;

                    Rs2Bank.withdrawOne("tinderbox");
                    Rs2Walker.walkTo(initialPlayerLocation, 4);
                }

                if (config.hopWhenPlayerDetected()) {
                    Rs2Player.logoutIfPlayerDetected(1, 10);
                    return;
                }

                if (Rs2Equipment.isWearing("Dragon axe"))
                    Rs2Combat.setSpecState(true, 1000);
                if (Microbot.isMoving() || Microbot.isAnimating() || Microbot.getClient().getLocalPlayer().isInteracting() || Microbot.pauseAllScripts) return;
                if (Rs2Inventory.isFull()) {
                    if (config.burnLogs()){
                        if (Rs2GameObject.findGameObjectByLocation(Rs2Player.getWorldLocation()) != null){
                            var tiles = Rs2Tile.getTilesAroundPlayer(10)
                                    .stream()
                                    .filter(x -> Rs2GameObject.findGameObjectByLocation(WorldPoint.fromLocal(Microbot.getClient(), x)) == null)
                                    .sorted(Comparator.comparing(x -> x.distanceTo(Rs2Player.getLocalLocation())))
                                    .collect(Collectors.toList());

                            if (tiles.isEmpty()){
                                System.out.println("No free tile nearby.");
                            }
                            else {
                                Rs2Walker.walkFastLocal(tiles.get(0));
                                Rs2Player.waitForWalking();
                            }
                        }
                    }
                    else if (config.hasAxeInventory()) {
                        Rs2Inventory.dropAll(x -> !x.name.contains("axe"));
                        return;
                    } else {
                        Rs2Inventory.dropAll();
                        return;
                    }
                }
                GameObject tree = Rs2GameObject.findClosestObjects(config.TREE().getName(), true, config.distanceToStray(), getInitialPlayerLocation()).get(0);

                if (config.burnLogs() && (Rs2Inventory.isFull() || Random.random(1, 100) > 70)){
                    while (Rs2Inventory.hasItem("log")){
                        var groundLogs = Arrays.stream(Rs2GroundItem.getAll(4)).filter(x -> x.getTileItem().getOwnership() == TileItem.OWNERSHIP_SELF).collect(Collectors.toList());
                        for(var groundLog : groundLogs){
                            Rs2GroundItem.loot(groundLog);
                        }

                        var objectOnTile = Rs2GameObject.findGameObjectByLocation(Rs2Player.getWorldLocation());

                        if (objectOnTile != null)
                            break;

                        var tinderbox = Rs2Inventory.get("tinderbox");
                        var log = Rs2Inventory.get("log");

                        var beforeLightingPos = Rs2Player.getWorldLocation();
                        Rs2Inventory.combine(tinderbox, log);
                        sleepUntil(() -> !Rs2Player.isWalking()
                                && !Rs2Player.isInteracting()
                                && !Rs2Player.getWorldLocation().equals(beforeLightingPos), 30_000);

                        sleep(100, 400);
                    }
                } else if (tree != null){
                    Rs2GameObject.interact(tree, config.TREE().getAction());
                }else {
                    System.out.println("No trees in zone");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }
}
