package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TempleOfTheEyeTask extends AccountBuilderQuestTask {
    public TempleOfTheEyeTask(){
        super(QuestHelperQuest.TEMPLE_OF_THE_EYE,
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true));
    }

    ArrayList<TileObject> objectOrder = new ArrayList<>();

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == 43768){
            stopQuest();

            for (var object : objectOrder){
                var objectComp = Rs2GameObject.convertGameObjectToObjectComposition(object.getId());
                var actions = objectComp.getImpostorIds() != null ? objectComp.getImpostor().getActions() : objectComp.getActions();

                if (Arrays.asList(actions).contains("Touch")){
                    Rs2GameObject.interact(object);
                    Rs2Player.waitForWalking();
                    sleep(500);
                }
            }

            for (var object : step.getObjects().stream().filter(x -> !objectOrder.contains(x)).sorted(Comparator.comparingInt(x -> Random.random(1, 100))).collect(Collectors.toList())){
                Rs2GameObject.interact(object);
                Rs2Player.waitForWalking();
                sleep(500);
                var objectComp = Rs2GameObject.convertGameObjectToObjectComposition(object.getId());
                var actions = objectComp.getImpostorIds() != null ? objectComp.getImpostor().getActions() : objectComp.getActions();

                if (Arrays.asList(actions).contains("Touch")){
                    if (!objectOrder.isEmpty())
                        return;
                } else {
                    objectOrder.add(object);
                    break;
                }
            }
        } else if (step.objectID == 2147){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2170, 4735, 702, 138, 0)) == 0){
                stopQuest();

                handleTutorial();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();


        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2876, 4802, 62, 62, 0)) == 0
                && (Rs2Npc.getNpc("Portal", false) != null
                || Rs2GameObject.findObjectById(34779) != null)){
            stopQuest();
            Rs2Walker.setTarget(null);
            var npc = Rs2Npc.getNpc("Portal", false);
            var go = Rs2GameObject.findObjectById(34779);
            if (npc != null)
                Rs2Npc.interact(npc, "Use");
            else if (go != null)
                Rs2GameObject.interact(go, "Use");
            Rs2Player.waitForWalking();
            startupQuest();
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (MQuestScript.getFullText(step).contains("Tell Wizard Traiborn the solution")){
            if (Global.sleepUntilTrue(() -> Rs2Widget.isWidgetVisible(162, 42), 10, 10000)){
                stopQuest();
                Rs2Keyboard.typeString("11");
                sleep(500, 800);
                Rs2Keyboard.enter();
                sleepUntil(Rs2Dialogue::isInDialogue, 2000);
                startupQuest();
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Complete the Guardians of the Rift tutorial.")){
            handleTutorial();
        }
    }

    private void handleTutorial(){
        if (Rs2Dialogue.isInDialogue() || Rs2Player.isAnimating()) return;

        if (!Rs2Inventory.contains(ItemID.CHISEL) && Rs2GroundItem.loot(ItemID.CHISEL)
                || !Rs2Inventory.contains(ItemID.BRONZE_PICKAXE) && Rs2GroundItem.loot(ItemID.BRONZE_PICKAXE)) {
            Rs2Player.waitForWalking();
            return;
        }

        var npc = Microbot.getClient().getHintArrowNpc();
        if (npc != null){
            Rs2Npc.interact(npc);
            Rs2Player.waitForWalking();
            return;
        }

        var worldPoint = Microbot.getClient().getHintArrowPoint();
        if (worldPoint != null){
            if (Microbot.getClient().isInInstancedRegion()){
                var localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
                worldPoint = WorldPoint.fromLocalInstance(Microbot.getClient(), localPoint);
            }

            var object = Rs2GameObject.getAll()
                    .stream().filter(x -> {
                        if (x instanceof GameObject) {
                            GameObject gameObject = (GameObject) x;
                            var point = WorldPoint.fromScene(Microbot.getClient(), gameObject.getSceneMinLocation().getX(), gameObject.getSceneMinLocation().getY(), gameObject.getPlane());
                            return Microbot.getClient().getHintArrowPoint().distanceTo(new WorldArea(
                                    point,
                                    gameObject.sizeX(),
                                    gameObject.sizeY())) == 0;
                        } else
                            return x.getWorldLocation().equals(Microbot.getClient().getHintArrowPoint());
                    }).findFirst().orElse(null);
            if (object != null){
                if (Rs2Player.getWorldLocation().distanceTo(worldPoint) > 12){
                    Rs2Walker.walkTo(worldPoint, 2);
                    Rs2Player.waitForWalking();
                    return;
                }

                var objectComp = Rs2GameObject.convertGameObjectToObjectComposition(object);
                if (objectComp.getName().contains("Guardian of")){
                    var splits = objectComp.getName().split(" ");
                    var type = splits[splits.length - 1];

                    var item = Rs2Inventory.get(x -> x.name.toLowerCase().contains("portal talisman")
                            && x.name.toLowerCase().contains(type.toLowerCase()));
                    if (item != null)
                        Rs2Inventory.use(item);
                }

                Rs2GameObject.interact(object);
                Rs2Player.waitForWalking();
            }
        }
    }
}
