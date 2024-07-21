package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryState;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeSlots;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public abstract class AccountBuilderTask {
    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    protected boolean canceled;
    protected boolean running = false;

    protected boolean memberOnly = true;
    public boolean blockStuckPrevention = false;
    protected Skill skill = null;
    protected int minLevel = 0;
    protected int maxLevel = Integer.MAX_VALUE;
    protected boolean ignoreUntradables = false;
    @Getter
    protected AccountBuilderTask followUpTask = null;

    protected int minTickTime = 500;
    protected int maxTickTime = 1500;

    protected ArrayList<ItemRequirement> itemRequirements = new ArrayList<>();

    public abstract String getName();

    public boolean requirementsMet(){
        if (skill != null){
            var level = Microbot.getClient().getRealSkillLevel(skill);

            if (level < minLevel || level > maxLevel)
                return false;
        }

        if (memberOnly && !Rs2Player.isMember())
            return false;

        if (!itemRequirements.isEmpty()){
            var gpRequired = getMissingItemPrice();
            if (gpRequired == Integer.MAX_VALUE ||  gpRequired > 0 && !Rs2Bank.hasBankItem("Coins", gpRequired))
                return false;
        }

        return true;
    }

    public boolean isCompleted(){
        return canceled;
    }

    public void doTaskCleanup(boolean shutdown) {
        if (scheduledFuture != null && !scheduledFuture.isDone())
            scheduledFuture.cancel(true);

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles();
        running = false;

        inventoryCleared = false;
        itemsBought = new ArrayList<>();
    }

    public void init(){
        canceled = false;
    }

    public void tick(){ }

    public void run(){
        running = true;

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            try {
                if (Microbot.enableAutoRunOn)
                    Rs2Player.toggleRunEnergy(true);

                if (Rs2Widget.getWidget(15269889) != null) {
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                }

                sleep(minTickTime, maxTickTime);

                if (Microbot.pauseAllScripts || !Microbot.isLoggedIn())
                    return;

                if (Rs2Inventory.contains(ItemID.LAMP)){
                    Rs2Tab.switchToInventoryTab();
                    sleep(500, 1000);
                    Rs2Inventory.interact(ItemID.LAMP, "Rub");
                    sleepUntil(() -> Rs2Widget.isWidgetVisible(240, 0), 5000);
                    Rs2Widget.clickWidget(240, 14); // Slayer
                    sleep(500, 1000);
                    Rs2Widget.clickWidget(240, 26);
                }

                tick();
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace(System.out);
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    public boolean doTaskPreparations(){ return true; }

    public void cancel(){
        canceled = true;
        System.out.printf("[AIO Account builder] Task '%s' canceled%n", getName());
    }

    protected void addRequirement(int id, int quantity){
        itemRequirements.add(new ItemRequirement("", id, quantity));
    }

    protected void addRequirement(int id, int quantity, int restockAmount){
        itemRequirements.add(new ItemRequirement("", id, quantity, restockAmount));
    }

    protected void addRequirement(ItemCollections collection, int quantity){
        itemRequirements.add(new ItemRequirement("", collection, quantity));
    }

    protected void addRequirement(int id, boolean equip){
        itemRequirements.add(new ItemRequirement("", id, 1, equip));
    }

    protected void addRequirement(ItemCollections collection, boolean equip){
        itemRequirements.add(new ItemRequirement("", collection, 1, equip));
    }

    public void onIdleMove() { }
    public void onChatMessage(ChatMessage chatMessage) { }
    public void onGameObjectSpawned(GameObjectSpawned event){ }
    public void onGameTick(GameTick gameTick) { }
    public void onGameStateChanged(GameStateChanged event) { }
    public void onWallObjectSpawned(WallObjectSpawned event) { }
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) { }
    public void onVarbitChanged(VarbitChanged event) { }
    public void onNpcDespawned(NpcDespawned npcDespawned) { }

    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void sleep(int time, int maxTime) {
        try {
            Thread.sleep(Random.random(time, maxTime));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    protected boolean sleepUntil(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < time);
        return done;
    }

    protected boolean isQuestCompleted(QuestHelperQuest questHelperQuest){
        return Microbot.getClientThread().runOnClientThread(() -> questHelperQuest.getQuestHelper().isCompleted());
    }

    protected boolean inventoryCleared = false;

    protected boolean clearInventory(Integer... except){
        if (inventoryCleared || Rs2Inventory.isEmpty()) {
            inventoryCleared = true;
            return true;
        }

        if (!Rs2Bank.walkToBankAndUseBank())
            return false;

        if (except.length > 0)
            inventoryCleared = Rs2Bank.depositAllExcept(except);
        else{
            Rs2Bank.depositAll();
            if (Rs2Inventory.isEmpty())
                inventoryCleared = true;
        }
        return inventoryCleared;
    }

    protected int getMissingItemPrice(){
        int total = 0;

        for (var itemRequirement : itemRequirements)
            if (itemRequirement.getId() != -1
                    && itemRequirement.getAllIds().stream().noneMatch(x -> Rs2Bank.hasBankItem(x, itemRequirement.getQuantity()))
                    && itemRequirement.getAllIds().stream().noneMatch(x -> Rs2Inventory.hasItemAmount(x, itemRequirement.getQuantity()))
                    && (itemRequirement.getQuantity() > 1 || itemRequirement.getAllIds().stream().noneMatch(Rs2Equipment::isWearing))) {
                var tradable = itemRequirement.getAllIds().stream().anyMatch(x -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(x)).isTradeable());
                if (tradable)
                    total += Microbot.getClientThread().runOnClientThread(() -> itemRequirement.getAllIds().stream().mapToInt(x -> Microbot.getItemManager().getItemPriceWithSource(x, false)).filter(x -> x > 0).min()).orElseThrow() * (itemRequirement.getRestockAmount() == -1 ? itemRequirement.getQuantity() : itemRequirement.getRestockAmount());
                else if (!ignoreUntradables && !itemRequirement.isObtainable())
                    return Integer.MAX_VALUE;
            }

        // Fixed increase for possible overpay
        total = (int) (total * 1.5);

        return total;
    }

    protected ArrayList<Integer> itemsBought = new ArrayList<>();

    protected boolean withdrawBuyItems(){
        if (Rs2Bank.bankItems == null || Rs2Bank.bankItems.isEmpty()){
            Rs2Bank.walkToBankAndUseBank();
            return false;
        }

        var itemsToBuy = itemRequirements.stream().filter(x -> x.getId() != -1
                && !x.isObtainable()
                && !itemsBought.contains(x.getId())
                && !Rs2Inventory.hasItemAmount(x.getId(), x.getQuantity())
                && x.getAllIds().stream().noneMatch(Rs2Equipment::isWearing)
                && !Rs2Bank.hasBankItem(x.getId(), x.getQuantity())
                && x.getAllIds().stream().noneMatch(y -> Rs2Inventory.hasItemAmount(y, x.getQuantity()) || Rs2Bank.hasBankItem(y, x.getQuantity()) || itemsBought.contains(y))).collect(Collectors.toList());
        if (!itemsToBuy.isEmpty()){
            if (!Rs2GrandExchange.walkToGrandExchange() || !Rs2GrandExchange.openExchange())
                return false;

            var minClicks = 2;
            var clicks = 4;
            boolean bought = false;
            while (!bought && !canceled){
                int itemToBuy = itemsToBuy.get(0).getAllIds().stream()
                        .filter(x -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(x).isTradeable()))
                        .collect(Collectors.toMap(x -> x, x -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemPriceWithSource(x, false))))
                        .entrySet().stream().filter(x -> x.getValue() > 0).min(Map.Entry.comparingByValue()).orElse(null).getKey();
                var itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(itemToBuy));

                var amountToBuy = itemsToBuy.get(0).getQuantity() - Rs2Bank.bankItems.stream().filter(x -> x.id == itemToBuy).map(x -> x.quantity).findFirst().orElse(0);
                Rs2GrandExchange.buyItemAboveXPercent(itemComposition.getName(), itemsToBuy.get(0).getRestockAmount() == -1 ? amountToBuy : itemsToBuy.get(0).getRestockAmount(), minClicks, clicks);
                sleepUntil(() -> Rs2GrandExchange.hasBoughtOffer(itemToBuy), 10000);

                if (Rs2GrandExchange.hasBoughtOffer(itemToBuy)){
                    Rs2GrandExchange.collectToBank();
                    bought = true;
                    itemsBought.add(itemToBuy);
                } else {
                    var slot = Arrays.stream(GrandExchangeSlots.values()).filter(x -> Arrays.stream(Objects.requireNonNull(Rs2GrandExchange.getSlot(x)).getDynamicChildren()).anyMatch(y -> y.getItemId() == itemToBuy)).findFirst().orElse(null);
                    assert slot != null;
                    var slotWidget = Rs2GrandExchange.getSlot(slot);
                    assert slotWidget != null;

                    Microbot.doInvoke(new NewMenuEntry(2, slotWidget.getId(), MenuAction.CC_OP.getId(), 2, -1, "Abort offer"), slotWidget.getBounds());
                    sleepUntil(() -> Arrays.stream(Microbot.getClient().getGrandExchangeOffers()).anyMatch(x -> x.getItemId() == itemToBuy && x.getState() == GrandExchangeOfferState.CANCELLED_BUY), 5000);
                    sleep(1000, 2000);
                    Rs2GrandExchange.collectToBank();
                    sleep(100, 200);

                    clicks *= 2;
                    minClicks *= 2;
                }
            }
            return false;
        }

        for (var item : itemRequirements){
            if (item.isEquip() && item.getAllIds().stream().anyMatch(Rs2Equipment::isWearing) || item.getId() == -1 || item.isObtainable())
                continue;

            int id;
            if (!Rs2Bank.hasBankItem(item.getId(), item.getQuantity()) && !Rs2Inventory.hasItemAmount(item.getId(), item.getQuantity()))
                id = item.getAllIds().stream().filter(x -> Rs2Inventory.hasItemAmount(x, item.getQuantity()) || Rs2Bank.hasBankItem(x, item.getQuantity())).findFirst().orElse(-1);
            else
                id = item.getId();

            if (Rs2Inventory.hasItemAmount(id, item.getQuantity()))
                continue;

            if (Rs2Inventory.getEmptySlots() < item.getQuantity() && !Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(id).isStackable())
                    || Rs2Inventory.getEmptySlots() == 0)
                continue;

            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            var invItem = Rs2Inventory.get(id);
            if (invItem == null || !Rs2Inventory.hasItemAmount(id, item.getQuantity(), invItem.isStackable())){
                if (item.getQuantity() == 1) {
                    if (item.isEquip()) {
                        Rs2Bank.withdrawItem(id);
                        Rs2Inventory.waitForInventoryChanges();
                        var oldSlot = Rs2Inventory.get(id).slot;
                        Rs2Bank.wearItem(id);
                        Rs2Inventory.waitForInventoryChanges();
                        var newItem = Rs2Inventory.getItemInSlot(oldSlot);
                        if (newItem != null)
                            Rs2Bank.depositOne(newItem.id);
                    } else
                        Rs2Bank.withdrawItem(id);
                } else
                    Rs2Bank.withdrawX(id, item.getQuantity() - Rs2Inventory.count(id));
                return false;
            }
        }

        return true;
    }
}
