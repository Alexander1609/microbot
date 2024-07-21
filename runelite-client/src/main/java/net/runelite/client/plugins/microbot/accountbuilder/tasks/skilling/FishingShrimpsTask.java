package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.validateInteractable;

public class FishingShrimpsTask extends AccountBuilderTask {
    public FishingShrimpsTask(){
        skill = Skill.FISHING;
        maxLevel = 25;
        memberOnly = false;

        minTickTime = 50;
        maxTickTime = 50;
    }

    @Override
    public String getName() {
        return "Fishing: Shrimps";
    }

    public String[] items = { "Small fishing net", "Tinderbox" };
    public String[] rawFish = { "Raw shrimps", "Raw anchovies" };

    @Override
    public boolean doTaskPreparations() {
        if (!Arrays.stream(items).allMatch(Rs2Inventory::contains)){
            Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_TOP);
            Rs2Bank.useBank();
            Rs2Bank.depositAll(x -> Arrays.stream(items).noneMatch(y -> x.name.equals(y)));

            for (var item : items) {
                if (!Rs2Inventory.contains(item))
                    Rs2Bank.withdrawOne(item);
            }

            return false;
        }

        return true;
    }

    private long lastAction = 0;
    private long nextWait = 500;

    @Override
    public void tick() {
        if (Rs2Player.isAnimating() || Rs2Player.isInteracting() || Rs2Player.isWalking() || Microbot.isGainingExp){
            lastAction = System.currentTimeMillis();
            return;
        }

        if (lastAction + nextWait >= System.currentTimeMillis())
            return;
        else
            nextWait = net.runelite.client.plugins.microbot.util.math.Random.random(500, 1000);

        if (Rs2Inventory.isFull() || Rs2Inventory.get(x -> !Arrays.asList(ArrayUtils.addAll(items, rawFish)).contains(x.name)) != null){
            if (Rs2Inventory.get(rawFish) != null){
                if (!isQuestCompleted(QuestHelperQuest.COOKS_ASSISTANT)){
                    var logsArea = new WorldArea(3205, 3224, 2, 2, 2);
                    if (logsArea.distanceTo(Rs2Player.getWorldLocation()) > 10){
                        var points = logsArea.toWorldPointList();
                        var index = new Random().nextInt(points.size());
                        Rs2Walker.walkTo(points.get(index));
                    }

                    var fire = Rs2GameObject.getGameObjects(ObjectID.FIRE_26185).stream().findFirst().orElse(null);
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
                } else {
                    if (!Rs2Walker.walkTo(new WorldPoint(3211, 3215, 0)))
                        return;

                    var item = Rs2Inventory.get(rawFish).name;
                    Rs2Inventory.use(item);
                    Rs2GameObject.interact("Cooking range");
                }

                sleepUntil(() -> Rs2Widget.getWidget(17694734) != null, 3000);
                sleep(600, 1600);
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                Rs2Player.waitForAnimation();
            } else {
                Rs2Bank.walkToBank(BankLocation.LUMBRIDGE_TOP);
                Rs2Bank.useBank();
                Rs2Bank.depositAll(x -> Arrays.stream(items).noneMatch(y -> x.name.contains(y)));
            }
        }
        else {
            var shrimpArea = new WorldArea(3241, 3153, 3, 3, 0);
            if (shrimpArea.distanceTo(Rs2Player.getWorldLocation()) > 20) {
                var points = shrimpArea.toWorldPointList();
                var index = new Random().nextInt(points.size());
                Rs2Walker.walkTo(points.get(index));

                return;
            }

            for (var spot : FishingSpot.SHRIMP.getIds()) {
                var npc = Rs2Npc.getNpc(spot);
                if (npc != null && !Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
                    validateInteractable(npc);
                }
                Rs2Npc.interact(npc, "net");
            }
        }
    }
}
