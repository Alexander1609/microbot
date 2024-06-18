package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.*;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MisthalinMysteryTask extends AccountBuilderTask {
    QuestStep currentStep;
    GameObject targetWardrobe;
    long targetWardrobeTime;

    public MisthalinMysteryTask(){
        quest = QuestHelperQuest.MISTHALIN_MYSTERY;
    }

    @Override
    public void run() {
        super.run();

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            try {
                var step = quest.getQuestHelper().getCurrentStep();
                if (step != null)
                    currentStep = step;

                if (Rs2Widget.hasWidget("Count Check"))
                    Rs2Widget.clickWidget(Rs2Widget.getWidget(219, 1).getChild(1 + new Random().nextInt(2)).getId());

                while (currentStep instanceof ConditionalStep)
                    currentStep = currentStep.getActiveStep();

                if (currentStep instanceof ObjectStep) {
                    var objectStep = (ObjectStep) currentStep;

                    if (objectStep.objectID == NullObjectID.NULL_29649 && objectStep.getText().stream().anyMatch(x -> x.contains("Use the bucket"))){
                        Rs2Inventory.use(ItemID.BUCKET);
                        Rs2GameObject.interact(NullObjectID.NULL_29649);
                    } else if (objectStep.objectID == NullObjectID.NULL_29650 && objectStep.getText().stream().anyMatch(x -> x.contains("Use a knife"))){
                        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1628, 4825, 5, 10, 0)) > 0){
                            if (isQuestRunning())
                                stopQuest();

                            if (!Rs2Player.getWorldLocation().equals(new WorldPoint(1633, 4831, 0))) {
                                Rs2Walker.walkFastCanvas(new WorldPoint(1633, 4831, 0));
                                Rs2Player.waitForWalking();
                            }

                            Rs2Walker.walkTo(new WorldPoint(1628, 4830, 0), 2);
                        }
                        else{
                            Rs2Inventory.use("knife");
                            Rs2GameObject.interact(NullObjectID.NULL_29650);

                            if (!isQuestRunning())
                                startupQuest();
                        }
                    } else if (objectStep.getText().stream().anyMatch(x -> x.contains("Light the"))){
                        Rs2Inventory.use("tinderbox");
                        Rs2GameObject.interact(objectStep.objectID);
                    } else if (objectStep.objectID == ObjectID.DEAD_TREE_30150){
                        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1648, 4826, 4, 18, 0)) == 0)
                            Rs2Walker.walkTo(new WorldPoint(1650, 4845, 0), 1);
                    } else if (objectStep.objectID == NullObjectID.NULL_29659){
                        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1629, 4831, 11, 15, 0)) == 0){
                            if (isQuestRunning())
                                stopQuest();

                            Rs2Walker.walkTo(new WorldPoint(1639, 4828, 0), 2);
                        }
                        else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1641, 4833, 7, 6, 0)) == 0){
                            Rs2Inventory.use("knife");
                            Rs2GameObject.interact(NullObjectID.NULL_29659);
                            Rs2GameObject.interact(NullObjectID.NULL_29659, "Search");
                        }
                        else if (!isQuestRunning())
                            startupQuest();
                    } else if (objectStep.objectID == NullObjectID.NULL_29657){
                        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1648, 4828, 4, 17, 0)) > 0
                                && Rs2Player.getWorldLocation().distanceTo(new WorldPoint(1647, 4828, 0)) > 3){
                            if (isQuestRunning())
                                stopQuest();

                            Rs2Walker.walkTo(new WorldPoint(1650, 4840, 0), 2);
                        } else if (!isQuestRunning())
                            startupQuest();
                    } else if (objectStep.objectID == NullObjectID.NULL_29658 && objectStep.getText().stream().anyMatch(x -> x.contains("Search"))){
                        Rs2GameObject.interact(NullObjectID.NULL_29658, "Search");
                    }
                } else if (currentStep instanceof DetailedQuestStep && currentStep.getText().stream().anyMatch(x -> x.contains("Read the notes."))){
                    if (Rs2Inventory.contains(ItemID.NOTES_21058))
                        Rs2Inventory.interact(ItemID.NOTES_21058, "Read");
                    else if (Rs2Inventory.contains(ItemID.NOTES_21057))
                        Rs2Inventory.interact(ItemID.NOTES_21057, "Read");
                    else
                        Rs2Inventory.interact(ItemID.NOTES_21056, "Read");
                } else if (currentStep.getText().stream().anyMatch(x -> x.contains("This puzzle requires you to move the"))){
                    if (ShortestPathPlugin.getMarker() != null)
                        ShortestPathPlugin.exit();

                    var mirror = Rs2Npc.getNpc(7641);

                    if (mirror == null)
                        return;

                    var mirrorLocation = WorldPoint.fromLocalInstance(Microbot.getClient(), mirror.getLocalLocation());
                    // <li>0 is true South</li><li>512 is true West</li><li>1024 is true North</li><li>1536 is true East</li>
                    var orientation = mirror.getOrientation();

                    if (targetWardrobe == null || targetWardrobeTime < System.currentTimeMillis()) {
                        GameObject nextWardrobe = null;
                        while (nextWardrobe == null || nextWardrobe.getWorldLocation().getX() == mirror.getWorldLocation().getX()
                                || nextWardrobe.getWorldLocation().getY() == mirror.getWorldLocation().getY()){
                            var wardrobes = Rs2GameObject.getGameObjects(30141);
                            nextWardrobe = wardrobes.get(new Random().nextInt(wardrobes.size()));
                            var nextWardrobeLocation = WorldPoint.fromLocalInstance(Microbot.getClient(), nextWardrobe.getLocalLocation());

                            if (mirrorLocation.getY() == 4828 && nextWardrobeLocation.getY() == 4825
                                || mirrorLocation.getX() == 1624 && nextWardrobeLocation.getX() == 1627
                                || mirrorLocation.getY() == 4831 && nextWardrobeLocation.getY() == 4834
                                || mirrorLocation.getX() == 1622 && nextWardrobeLocation.getX() == 1619)
                                nextWardrobe = null;
                        }
                        targetWardrobe = nextWardrobe;
                        targetWardrobeTime = Long.MAX_VALUE;
                    }

                    var targetLocation = WorldPoint.fromLocalInstance(Microbot.getClient(), targetWardrobe.getLocalLocation());
                    System.out.printf("Mirror: %d %d%n", mirrorLocation.getX(), mirrorLocation.getY());
                    System.out.printf("Target: %d %d%n", targetLocation.getX(), targetLocation.getY());

                    // Check if vertical wardrobe
                    if (targetWardrobe.getOrientation() == 512 || targetWardrobe.getOrientation() == 1536) {
                        if (targetLocation.getX() > mirrorLocation.getX()) {
                            walkCanvasCheck(mirrorLocation.dx(-1));
                            Rs2Npc.interact(mirror);
                            Rs2Player.waitForAnimation();
                        } else if (targetLocation.getX() < mirrorLocation.getX()) {
                            walkCanvasCheck(mirrorLocation.dx(1));
                            Rs2Npc.interact(mirror);
                            Rs2Player.waitForAnimation();
                        } else {
                            if (targetLocation.getY() > mirrorLocation.getY() && orientation != 1024) {
                                walkCanvasCheck(mirrorLocation.dy(-1));
                                Rs2Npc.interact(mirror);
                                Rs2Player.waitForAnimation();
                            } else if (targetLocation.getY() < mirrorLocation.getY() && orientation != 0) {
                                walkCanvasCheck(mirrorLocation.dy(1));
                                Rs2Npc.interact(mirror);
                                Rs2Player.waitForAnimation();
                            } else if (targetWardrobeTime == Long.MAX_VALUE) {
                                targetWardrobeTime = System.currentTimeMillis() + 10_000 + new Random().nextInt(10_000);
                            }
                        }
                    } else {
                        if (targetLocation.getY() > mirrorLocation.getY()) {
                            walkCanvasCheck(mirrorLocation.dy(-1));
                            Rs2Npc.interact(mirror);
                            Rs2Player.waitForAnimation();
                        } else if (targetLocation.getY() < mirrorLocation.getY()) {
                            walkCanvasCheck(mirrorLocation.dy(1));
                            Rs2Npc.interact(mirror);
                            Rs2Player.waitForAnimation();
                        } else {
                            if (targetLocation.getX() > mirrorLocation.getX() && orientation != 1536) {
                                walkCanvasCheck(mirrorLocation.dx(-1));
                                Rs2Npc.interact(mirror);
                                Rs2Player.waitForAnimation();
                            } else if (targetLocation.getX() < mirrorLocation.getX() && orientation != 512) {
                                walkCanvasCheck(mirrorLocation.dx(1));
                                Rs2Npc.interact(mirror);
                                Rs2Player.waitForAnimation();
                            } else if (targetWardrobeTime == Long.MAX_VALUE){
                                targetWardrobeTime = System.currentTimeMillis() + 10_000 + new Random().nextInt(10_000);
                            }
                        }
                    }
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void walkCanvasCheck(WorldPoint point){
        if (!Rs2Player.getWorldLocation().equals(point)){
            Rs2Walker.walkFastCanvas(point);
            Rs2Player.waitForWalking();
        }
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getMessage().equals("The mirror can't go any further in that direction."))
            targetWardrobeTime = 0;
    }

    @Override
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (event.getGameObject().getId() == 30142)
            targetWardrobeTime = System.currentTimeMillis() + 1_000;
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 21){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        return true;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        while (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1601, 4793, 70, 70, 0)) == 0){
            if (!Rs2Walker.walkTo(new WorldPoint(1637, 4805, 0))){
                sleep(1000, 1500);
                continue;
            }

            Rs2GameObject.interact(30109);
            sleep(1000, 1500);
        }

        while (Rs2Inventory.contains(ItemID.NOTES_21058)){
            if (Rs2Inventory.contains(ItemID.NOTES_21056)){
                Rs2Inventory.interact(ItemID.NOTES_21056, "destroy");
                sleep(1000, 1500);
                continue;
            }

            if (Rs2Inventory.contains(ItemID.NOTES_21057)){
                Rs2Inventory.interact(ItemID.NOTES_21057, "destroy");
                sleep(1000, 1500);
                continue;
            }

            Rs2Inventory.interact(ItemID.NOTES_21058, "destroy");
            sleep(1000, 1500);
        }

        super.doTaskCleanup(shutdown);
    }
}
