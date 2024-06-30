package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WaterfallQuestTask extends AccountBuilderQuestTask {
    private boolean clearedInventory = false;
    private Map<GameObject, Integer> pillars;

    public WaterfallQuestTask(){
        super(QuestHelperQuest.WATERFALL_QUEST);
    }

    @Override
    public void init() {
        super.init();

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(
                new Restriction(2576, 9882, 0),
                new Restriction(2577, 9882, 0)
        );
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 30;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.GLARIALS_TOMBSTONE){
            if (Arrays.stream(EquipmentInventorySlot.values()).anyMatch(Rs2Equipment::hasEquippedSlot)
                || Rs2Inventory.hasItem("rune")){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Bank.walkToBank(BankLocation.EAST_ARDOUGNE_NORTH) || !Rs2Bank.useBank())
                    return;

                Rs2Bank.depositEquipment();
                Rs2Bank.depositAllExcept(ItemID.GLARIALS_PEBBLE);
            } else if (!isQuestRunning())
                startupQuest();
        } else if (step.objectID == ObjectID.DOOR_2002){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2568, 9891, 0)) < 3)
                Rs2GameObject.interact(new WorldPoint(2568, 9893, 0), "Open");
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Leave Glarial's Tomb"))){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Equipment.hasEquipped(ItemID.GLARIALS_AMULET))
                Rs2Inventory.wear(ItemID.GLARIALS_AMULET);

            if (!Rs2Inventory.contains(ItemID.ROPE)){
                if (!Rs2Bank.walkToBankAndUseBank())
                    return;

                if (!Rs2Inventory.hasItemAmount(ItemID.AIR_RUNE, 6))
                    Rs2Bank.withdrawX(ItemID.AIR_RUNE, 6);
                else if (!Rs2Inventory.hasItemAmount(ItemID.WATER_RUNE, 6))
                    Rs2Bank.withdrawX(ItemID.WATER_RUNE, 6);
                else if (!Rs2Inventory.hasItemAmount(ItemID.EARTH_RUNE, 6))
                    Rs2Bank.withdrawX(ItemID.EARTH_RUNE, 6);
                else
                    Rs2Bank.withdrawItem(ItemID.ROPE);
            } else if (!isQuestRunning())
                startupQuest();
        } else if (step.getText().stream().anyMatch(x -> x.contains("Use 1 earth, water and air rune"))){
            if (isQuestRunning() && !Rs2Inventory.hasItem(ItemID.GLARIALS_AMULET))
                stopQuest();

            if (!Rs2Walker.walkTo(new WorldPoint(2566, 9912, 0), 5)) {
                Rs2Player.waitForWalking();
                return;
            }

            if (pillars == null)
                pillars = Arrays.stream(new WorldPoint[]{
                        new WorldPoint(2569, 9910, 0),
                        new WorldPoint(2569, 9912, 0),
                        new WorldPoint(2569, 9914, 0),
                        new WorldPoint(2562, 9910, 0),
                        new WorldPoint(2562, 9912, 0),
                        new WorldPoint(2562, 9914, 0)}).collect(Collectors.toMap(x -> {
                    var localPoint = LocalPoint.fromWorld(Microbot.getClient(), x);
                    var tile = Microbot.getClient().getScene().getTiles()[Microbot.getClient().getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
                    return tile.getGameObjects()[0];
                }, x -> 0));

            var pillar = pillars.entrySet().stream().filter(x -> x.getValue() < 3).findFirst().orElse(null);
            if (false && pillar != null){
                int id;
                if (pillar.getValue() < 1)
                    id = ItemID.AIR_RUNE;
                else if (pillar.getValue() < 2)
                    id = ItemID.WATER_RUNE;
                else
                    id = ItemID.EARTH_RUNE;

                var count = Rs2Inventory.get(id).quantity;
                Rs2Inventory.use(id);
                Rs2GameObject.interact(pillar.getKey());
                Rs2Player.waitForWalking(200);
                sleepUntil(() -> Rs2Dialogue.isInDialogue(), 5000);

                if (!Rs2Inventory.contains(id) || Rs2Inventory.get(id).quantity < count){
                    pillars.put(pillar.getKey(), pillar.getValue() + 1);
                }
            } else if (Rs2Equipment.hasEquipped(ItemID.GLARIALS_AMULET)){
                Microbot.doInvoke(new NewMenuEntry(-1, 25362449, MenuAction.CC_OP.getId(), 1, -1, "<col=ff9040>Glarial's amulet</col>"), new Rectangle(0, 0, 1, 1));
            } else if (!isQuestRunning())
                startupQuest();
            else {
                Rs2Inventory.useItemOnObject(ItemID.GLARIALS_AMULET, ObjectID.STATUE_OF_GLARIAL);
                Rs2Player.waitForWalking();
            }
        }
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        while (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2554, 9860, 59, 61, 0)) == 0){
            sleep(1000, 2000);

            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2594, 9901, 17, 18, 0)) == 0) {
                if (!Rs2Walker.walkTo(new WorldPoint(2604, 9901, 0)))
                    continue;

                Rs2GameObject.interact(new WorldPoint(2604, 9900, 0), "Open");
            }
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2594, 9893, 17, 8, 0)) == 0) {
                if (!Rs2Walker.walkTo(new WorldPoint(2606, 9893, 0)))
                    continue;

                Rs2GameObject.interact(new WorldPoint(2606, 9892, 0), "Open");
            }
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2563, 9894, 8, 8, 0)) == 0) {
                if (!Rs2Walker.walkTo(new WorldPoint(2568, 9894, 0)))
                    continue;

                Rs2GameObject.interact(new WorldPoint(2568, 9893, 0), "Open");
            } else {
                Rs2Walker.walkTo(new WorldPoint(2511, 3463, 0));
            }
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (Microbot.getClientThread().runOnClientThread(() -> QuestHelperQuest.WATERFALL_QUEST.getState(Microbot.getClient())) == QuestState.IN_PROGRESS)
            return true;

        if (!clearedInventory && Rs2Inventory.getEmptySlots() < 28){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();

            if (Rs2Inventory.getEmptySlots() == 28)
                clearedInventory = true;
            return false;
        }

        if (!Rs2Inventory.hasItemAmount("Coins", 400)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            var amount = Random.random(500, 600);
            if (Rs2Bank.hasBankItem("Coins", amount))
                Rs2Bank.withdrawX("Coins", amount);
            else
                cancel();
            return false;
        }

        if (!Rs2Inventory.containsAll(ItemID.WATER_RUNE, ItemID.EARTH_RUNE, ItemID.AIR_RUNE)){
            if (!Rs2Walker.walkTo(new WorldPoint(3014, 3258, 0), 4))
                return false;

            if (!Rs2Shop.isOpen())
                Rs2Shop.openShop("Betty");
            else if (!Rs2Inventory.contains(ItemID.WATER_RUNE))
                Rs2Shop.buyItem("Water rune", "10");
            else if (!Rs2Inventory.contains(ItemID.EARTH_RUNE))
                Rs2Shop.buyItem("Earth rune", "10");
            else if (!Rs2Inventory.contains(ItemID.AIR_RUNE))
                Rs2Shop.buyItem("Air rune", "10");

            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.ROPE)){
            if (!Rs2Walker.walkTo(new WorldPoint(2614, 3292, 0), 2))
                return false;

            if (!Rs2Shop.isOpen())
                Rs2Shop.openShop("Aemad");
            else
                Rs2Shop.buyItem("Rope", "1");

            return false;
        }

        return true;
    }
}
