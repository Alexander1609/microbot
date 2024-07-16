package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.WidgetStep;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TheEyesOfGlouphrieTask extends AccountBuilderQuestTask {
    public TheEyesOfGlouphrieTask(){
        super(QuestHelperQuest.THE_EYES_OF_GLOUPHRIE,
                new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF, 1));


        shapes.put(ItemID.RED_CIRCLE, new ItemRequirement("Red circle", ItemID.RED_CIRCLE));
        shapes.put(ItemID.ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.ORANGE_CIRCLE));
        shapes.put(ItemID.YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.YELLOW_CIRCLE));
        shapes.put(ItemID.GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.GREEN_CIRCLE));
        shapes.put(ItemID.BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.BLUE_CIRCLE));
        shapes.put(ItemID.INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.INDIGO_CIRCLE));
        shapes.put(ItemID.VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.VIOLET_CIRCLE));

        shapes.put(ItemID.RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.RED_TRIANGLE));
        shapes.put(ItemID.ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.ORANGE_TRIANGLE));
        shapes.put(ItemID.YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.YELLOW_TRIANGLE));
        shapes.put(ItemID.GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.GREEN_TRIANGLE));
        shapes.put(ItemID.BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.BLUE_TRIANGLE));
        shapes.put(ItemID.INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.INDIGO_TRIANGLE));
        shapes.put(ItemID.VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.VIOLET_TRIANGLE));

        shapes.put(ItemID.RED_SQUARE, new ItemRequirement("Red square", ItemID.RED_SQUARE));
        shapes.put(ItemID.ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.ORANGE_SQUARE));
        shapes.put(ItemID.YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.YELLOW_SQUARE));
        shapes.put(ItemID.GREEN_SQUARE, new ItemRequirement("Green square", ItemID.GREEN_SQUARE));
        shapes.put(ItemID.BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.BLUE_SQUARE));
        shapes.put(ItemID.INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.INDIGO_SQUARE));
        shapes.put(ItemID.VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.VIOLET_SQUARE));

        shapes.put(ItemID.RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.RED_PENTAGON));
        shapes.put(ItemID.ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.ORANGE_PENTAGON));
        shapes.put(ItemID.YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.YELLOW_PENTAGON));
        shapes.put(ItemID.GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.GREEN_PENTAGON));
        shapes.put(ItemID.BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.BLUE_PENTAGON));
        shapes.put(ItemID.INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.INDIGO_PENTAGON));
        shapes.put(ItemID.VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.VIOLET_PENTAGON));

        yellowCircleRedTri = new ItemRequirement("Yellow circle/red triangle", ItemID.RED_TRIANGLE);
        yellowCircleRedTri.addAlternates(ItemID.YELLOW_CIRCLE);
        greenCircleRedSquare = new ItemRequirement("Green circle/red square", ItemID.GREEN_CIRCLE);
        greenCircleRedSquare.addAlternates(ItemID.RED_SQUARE);
        blueCircleRedPentagon = new ItemRequirement("Blue circle/red pentagon", ItemID.BLUE_CIRCLE);
        blueCircleRedPentagon.addAlternates(ItemID.RED_PENTAGON);
        indigoCircleOrangeTriangle = new ItemRequirement("Indigo circle/orange triangle", ItemID.INDIGO_CIRCLE);
        indigoCircleOrangeTriangle.addAlternates(ItemID.ORANGE_TRIANGLE);
        yellowSquareGreenTriangle = new ItemRequirement("Yellow square/green triangle", ItemID.YELLOW_SQUARE);
        yellowSquareGreenTriangle.addAlternates(ItemID.GREEN_TRIANGLE);
        yellowPentagonBlueTriangle = new ItemRequirement("Yellow pentagon/blue triangle", ItemID.YELLOW_PENTAGON);
        yellowPentagonBlueTriangle.addAlternates(ItemID.BLUE_TRIANGLE);
        blueSquareGreenPentagon = new ItemRequirement("Blue square/green pentagon", ItemID.BLUE_SQUARE);
        blueSquareGreenPentagon.addAlternates(ItemID.GREEN_PENTAGON);

        shapeValues.put(1, shapes.get(ItemID.RED_CIRCLE));
        shapeValues.put(2, shapes.get(ItemID.ORANGE_CIRCLE));
        shapeValues.put(3, yellowCircleRedTri);
        shapeValues.put(4, greenCircleRedSquare);
        shapeValues.put(5, blueCircleRedPentagon);
        shapeValues.put(6, indigoCircleOrangeTriangle);
        shapeValues.put(7, shapes.get(ItemID.VIOLET_CIRCLE));
        shapeValues.put(8, shapes.get(ItemID.ORANGE_SQUARE));
        shapeValues.put(9, shapes.get(ItemID.YELLOW_TRIANGLE));
        shapeValues.put(10, shapes.get(ItemID.ORANGE_PENTAGON));
        shapeValues.put(12, yellowSquareGreenTriangle);
        shapeValues.put(15, yellowPentagonBlueTriangle);
        shapeValues.put(16, shapes.get(ItemID.GREEN_SQUARE));
        shapeValues.put(18, shapes.get(ItemID.INDIGO_TRIANGLE));
        shapeValues.put(20, blueSquareGreenPentagon);
        shapeValues.put(21, shapes.get(ItemID.VIOLET_TRIANGLE));
        shapeValues.put(24, shapes.get(ItemID.INDIGO_SQUARE));
        shapeValues.put(25, shapes.get(ItemID.BLUE_PENTAGON));
        shapeValues.put(28, shapes.get(ItemID.VIOLET_SQUARE));
        shapeValues.put(30, shapes.get(ItemID.INDIGO_PENTAGON));
        shapeValues.put(35, shapes.get(ItemID.VIOLET_PENTAGON));
    }

    HashMap<Integer, ItemRequirement> shapes = new HashMap<>();
    HashMap<Integer, ItemRequirement> shapeValues = new HashMap<>();
    ItemRequirement yellowCircleRedTri, greenCircleRedSquare, blueCircleRedPentagon, blueSquareGreenPentagon, indigoCircleOrangeTriangle, yellowSquareGreenTriangle, yellowPentagonBlueTriangle;
    HashMap<Integer, Set<Integer>> failedConversions = new HashMap<>();

    int tries = 0;

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == NullObjectID.NULL_17283){
            for (var req : step.getRequirements()){
                if (req instanceof ItemRequirement){
                    var itemReq = (ItemRequirement) req;
                    if (itemReq.getAllIds().stream().noneMatch(x -> Rs2Inventory.hasItemAmount(x, itemReq.getQuantity()))){
                        stopQuest();

                        var value = shapeValues.entrySet().stream().filter(x -> x.getValue().getAllIds().stream().allMatch(y -> itemReq.getAllIds().contains(y))).findFirst().get();
                        for (var shapeValue : shapeValues.entrySet()){
                            if (shapeValue.getKey() <= value.getKey()) continue;
                            if (failedConversions.containsKey(shapeValue.getKey()) && failedConversions.get(shapeValue.getKey()).contains(value.getKey())) continue;

                            if (Rs2Inventory.contains(shapeValue.getValue().getAllIds().toArray(new Integer[0]))){
                                if (!Rs2Walker.walkTo(2391, 9825, 0)) return;

                                Rs2GameObject.interact(NullObjectID.NULL_17283);
                                Rs2Player.waitForWalking();
                                sleep(500, 1000);
                                if (Rs2Widget.isWidgetVisible(164, 16)){
                                    var item = Rs2Inventory.get(x -> shapeValue.getValue().getAllIds().contains(x.id));
                                    var slot = Arrays.stream(Rs2Widget.getWidget(185, 0).getDynamicChildren()).filter(x -> x.getItemId() == item.id).findFirst().get().getIndex();
                                    Microbot.doInvoke(new NewMenuEntry(slot, 12124160, MenuAction.CC_OP.getId(), 1, item.id, item.name), new Rectangle(1, 1));
                                    sleep(500, 1000);
                                    Rs2Widget.clickWidget(111, 14);
                                    sleep(500, 1000);
                                    Rs2Widget.clickWidget(111, 13);
                                    sleep(500, 1000);

                                    var tries = 0;
                                    while (Arrays.stream(Rs2Widget.getWidget(111, 7).getStaticChildren()).noneMatch(x -> value.getValue().getAllIds().contains(x.getItemId())) && tries < 20){
                                        Rs2Widget.clickWidget(111, 13);
                                        sleep(500, 1000);
                                        tries++;
                                    }

                                    if (tries == 20){
                                        failedConversions.putIfAbsent(shapeValue.getKey(), new HashSet<>());
                                        failedConversions.get(shapeValue.getKey()).add(value.getKey());
                                    }

                                    Rs2Widget.clickWidget(111, 17);
                                }

                                return;
                            }
                        }

                        var npc = Rs2Npc.getNpc("Brimstail");
                        if (npc == null){
                            Rs2Walker.walkTo(new WorldPoint(2410, 9818, 0));
                            Rs2Player.waitForWalking();
                            return;
                        }

                        Rs2Inventory.dropAll(x -> shapes.containsKey(x.id) && step.getRequirements().stream().noneMatch(y -> y instanceof ItemRequirement && ((ItemRequirement)y).getAllIds().contains(x.id)));
                        sleep(500, 1000);
                        Rs2Npc.interact(npc, "Talk-to");
                        Rs2Player.waitForWalking();
                        sleepUntil(Rs2Dialogue::isInDialogue, 5000);
                        while (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 1000)){
                            if (Rs2Dialogue.hasContinue())
                                Rs2Dialogue.clickContinue();
                            else if (Rs2Widget.hasWidget("I can't work out what to do with these discs!"))
                                Rs2Widget.clickWidget("I can't work out what to do with these discs!");
                            else
                                Rs2Widget.clickWidget("I think Oaknock's machine is now unlocked, what do I do now?");

                            sleep(200, 400);
                        }

                        return;
                    }
                }
            }

            for (var req : step.getRequirements()){
                if (req instanceof ItemRequirement){
                    var itemReq = (ItemRequirement) req;
                    if (!Rs2Inventory.hasItemAmount(itemReq.getId(), itemReq.getQuantity())){
                        stopQuest();

                        Rs2GroundItem.loot(itemReq.getId());
                        Rs2Inventory.waitForInventoryChanges();
                        return;
                    }
                }
            }

            startupQuest();
        } else if (step.objectID == NullObjectID.NULL_17282){
            if (Rs2Widget.isWidgetVisible(164, 16)){
                var itemReq = (ItemRequirement)step.getRequirements().get(0);
                var item = Rs2Inventory.get(itemReq.getId());
                var slot = Arrays.stream(Rs2Widget.getWidget(185, 0).getDynamicChildren()).filter(x -> x.getItemId() == item.id).findFirst().get().getIndex();
                Microbot.doInvoke(new NewMenuEntry(slot, 12124160, MenuAction.CC_OP.getId(), 1, item.id, item.name), new Rectangle(1, 1));
                sleep(100, 200);
                Rs2Widget.clickWidget(145, 8);
                sleep(100, 200);
                Rs2Widget.clickWidget(145, 12);
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step instanceof WidgetStep){
            for (var req : step.getRequirements()){
                if (req instanceof ItemRequirement){
                    var itemReq = (ItemRequirement)step.getRequirements().get(0);
                    var item = Rs2Inventory.get(itemReq.getAllIds().toArray(new Integer[0]));
                    var slot = Arrays.stream(Rs2Widget.getWidget(185, 0).getDynamicChildren()).filter(x -> x.getItemId() == item.id).findFirst().get().getIndex();
                    Microbot.doInvoke(new NewMenuEntry(slot, 12124160, MenuAction.CC_OP.getId(), 1, item.id, item.name), new Rectangle(1, 1));
                    return;
                }
            }
        }
    }
}
