package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Setter;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeSlots;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
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
    @Setter
    ItemManager itemManager;

    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    protected boolean canceled;

    protected boolean memberOnly = true;
    public boolean blockStuckPrevention = false;
    protected Skill skill = null;
    protected int minLevel = 0;
    protected int maxLevel = Integer.MAX_VALUE;

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
            if (gpRequired > 0 && !Rs2Bank.hasBankItem("Coins", gpRequired))
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
    }

    public void init(){
        canceled = false;
    }

    public void tick(){ }

    public void run(){
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

    public void onChatMessage(ChatMessage chatMessage) { }
    public void onGameObjectSpawned(GameObjectSpawned event){ }
    public void onGameTick(GameTick gameTick) { }
    public void onGameStateChanged(GameStateChanged event) { }

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
        if (inventoryCleared || Rs2Inventory.isEmpty())
            return true;

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
                    && itemRequirement.getAllIds().stream().noneMatch(x -> Rs2Inventory.hasItemAmount(x, itemRequirement.getQuantity())))
                total += itemRequirement.getAllIds().stream().min(Comparator.comparing(x -> Microbot.getClientThread().runOnClientThread(() -> itemManager.getItemPriceWithSource(x, false)))).orElse(0) * itemRequirement.getQuantity();

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

        var itemsToBuy = itemRequirements.stream().filter(x -> !itemsBought.contains(x.getId())
                && !Rs2Inventory.hasItemAmount(x.getId(), x.getQuantity())
                && !Rs2Bank.hasBankItem(x.getId(), x.getQuantity())
                && x.getAllIds().stream().noneMatch(y -> Rs2Inventory.hasItemAmount(y, x.getQuantity()) || Rs2Bank.hasBankItem(y, x.getQuantity()) || itemsBought.contains(y))).collect(Collectors.toList());
        if (!itemsToBuy.isEmpty()){
            if (!Rs2GrandExchange.walkToGrandExchange() || !Rs2GrandExchange.openExchange())
                return false;

            var minClicks = 2;
            var clicks = 4;
            boolean bought = false;
            while (!bought && !canceled){
                Rs2GrandExchange.buyItemAboveXPercent(itemsToBuy.get(0).getName(), itemsToBuy.get(0).getQuantity(), minClicks, clicks);
                sleepUntil(() -> Rs2GrandExchange.hasBoughtOffer(itemsToBuy.get(0).getId()), 10000);

                if (Rs2GrandExchange.hasBoughtOffer(itemsToBuy.get(0).getId())){
                    Rs2GrandExchange.collectToBank();
                    bought = true;
                    itemsBought.add(itemsToBuy.get(0).getId());
                } else {
                    var slot = Arrays.stream(GrandExchangeSlots.values()).filter(x -> Arrays.stream(Objects.requireNonNull(Rs2GrandExchange.getSlot(x)).getDynamicChildren()).anyMatch(y -> y.getItemId() == itemsToBuy.get(0).getId())).findFirst().orElse(null);
                    assert slot != null;
                    var slotWidget = Rs2GrandExchange.getSlot(slot);
                    assert slotWidget != null;

                    Microbot.doInvoke(new NewMenuEntry(2, slotWidget.getId(), MenuAction.CC_OP.getId(), 2, -1, "Abort offer"), slotWidget.getBounds());
                    sleepUntil(() -> Arrays.stream(Microbot.getClient().getGrandExchangeOffers()).anyMatch(x -> x.getItemId() == itemsToBuy.get(0).getId() && x.getState() == GrandExchangeOfferState.CANCELLED_BUY), 5000);
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
            var id = item.getId();
            if (!Rs2Bank.hasBankItem(id, item.getQuantity()) && !Rs2Inventory.hasItemAmount(id, item.getQuantity()))
                id = item.getAllIds().stream().filter(x -> Rs2Inventory.hasItemAmount(x, item.getQuantity()) || Rs2Bank.hasBankItem(x, item.getQuantity())).findFirst().orElse(-1);

            if (Rs2Inventory.hasItemAmount(id, item.getQuantity()))
                continue;

            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            var invItem = Rs2Inventory.get(id);
            if (invItem == null || !Rs2Inventory.hasItemAmount(id, item.getQuantity(), invItem.isStackable())){
                if (item.getQuantity() == 1) {
                    if (item.isEquip())
                        Rs2Bank.withdrawAndEquip(id);
                    else
                        Rs2Bank.withdrawItem(id);
                } else
                    Rs2Bank.withdrawX(id, item.getQuantity() - Rs2Inventory.count(id));
                return false;
            }
        }

        return true;
    }
}
