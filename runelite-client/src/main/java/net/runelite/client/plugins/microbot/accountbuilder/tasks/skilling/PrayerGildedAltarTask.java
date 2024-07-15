package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.prayer.GildedAltarPlayerState;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;

import java.awt.*;
import java.util.Arrays;

public abstract class PrayerGildedAltarTask extends AccountBuilderTask {
    protected int bonesId;
    protected int amount;

    private int worldHopTries = 0;

    public PrayerGildedAltarTask(int bonesId, int amount){
        this.bonesId = bonesId;
        this.amount = amount;

        addRequirement(bonesId, amount);
        addRequirement(ItemCollections.COINS, Random.random(30_000, 50_000));
    }

    @Override
    public void init() {
        super.init();

        worldHopTries = 0;
    }

    @Override
    public boolean doTaskPreparations() {
        if (worldHopTries > 5)
            cancel();

        if (!clearInventory() || !withdrawBuyItems())
            return false;

        if (!hasNotedBones()){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            if (!Rs2Bank.hasWithdrawAsNote())
                Rs2Bank.setWithdrawAsNote();
            else
                Rs2Bank.withdrawX(bonesId, amount);

            return false;
        }

        if (!Rs2Walker.walkTo(new WorldPoint(2954, 3218, 0)))
            return false;

        if (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWorld()) != 330) {
            if (Rs2Bank.isOpen()){
                Rs2Bank.closeBank();
                return false;
            }

            worldHopTries++;
            var worlds = Microbot.getWorldService().getWorlds();

            Microbot.getClient().openWorldHopper();
            sleepUntil(() -> !Rs2Widget.isHidden(ComponentID.WORLD_SWITCHER_WORLD_LIST), 2000);
            sleep(500);

            Microbot.doInvoke(new NewMenuEntry(worlds.findWorld(330).getId(), 4522002, MenuAction.CC_OP.getId(), 1, -1, ""), new Rectangle(1, 1));
            sleep(10_000);

            if (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWorld()) == 330)
                return true;
        }

        return false;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        while (inHouse()){
            leaveHouse();
            sleepUntil(() -> !inHouse(), 4000);
        }

        super.doTaskCleanup(shutdown);
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || running && !hasNotedBones() && !hasUnNotedBones();
    }

    @Getter
    @Setter
    int skipTicks;
    private final int HOUSE_PORTAL_OBJECT = 4525;

    GildedAltarPlayerState state = GildedAltarPlayerState.IDLE;

    private boolean inHouse() {
        return Rs2Npc.getNpc("Phials") == null;
    }

    private boolean hasUnNotedBones() {
        return Rs2Inventory.hasUnNotedItem("bones");
    }

    private boolean hasNotedBones() {
        return Rs2Inventory.hasNotedItem("bones");
    }

    @Override
    public void tick() {
        if (Microbot.getClient().getGameState() != GameState.LOGGED_IN)
            return;

        if (skipTicks > 0)
        {
            if (Random.random(1, 7) != 2) {
                skipTicks--;
            }
            return;
        }

        if (!hasNotedBones() && !hasUnNotedBones()) {
            Microbot.showMessage("No bones found in your inventory");
            setSkipTicks(10);
            return;
        }

        calculateState();

        switch (state) {
            case LEAVE_HOUSE:
                leaveHouse();
                break;
            case UNNOTE_BONES:
                unnoteBones();
                break;
            case ENTER_HOUSE:
                enterHouse();
                break;
            case BONES_ON_ALTAR:
                bonesOnAltar();
                break;
        }
    }

    private void calculateState() {
        if (hasUnNotedBones() && !inHouse()) {
            state = GildedAltarPlayerState.ENTER_HOUSE;
        } else if (hasUnNotedBones() && inHouse()) {
            state = GildedAltarPlayerState.BONES_ON_ALTAR;
        } else if (!hasUnNotedBones() && !inHouse()) {
            state = GildedAltarPlayerState.UNNOTE_BONES;
        } else if (!hasUnNotedBones() && inHouse()) {
            state = GildedAltarPlayerState.LEAVE_HOUSE;
        }
    }

    public void leaveHouse() {
        System.out.println("Attempting to leave house...");

        if (Rs2GameObject.findObjectById(HOUSE_PORTAL_OBJECT) == null) {
            System.out.println("Not in house, HOUSE_PORTAL_OBJECT not found.");
            return;
        }

        // Switch to Settings tab
        Rs2Tab.switchToSettingsTab();
        setSkipTicks(2);

        // Click House Options
        if (Rs2Widget.isHidden(7602207)){
            Rs2Widget.clickWidget(116, 63);
        } else if (Rs2Widget.clickWidget(7602207)) {
            System.out.println("Clicked House Options button");
            setSkipTicks(2);
        } else {
            System.out.println("House Options button not found.");
            return;
        }

        // Click Leave House
        if (Rs2Widget.clickWidget(24248341)) {
            System.out.println("Clicked Leave House button");
            setSkipTicks(4);
        } else {
            System.out.println("Leave House button not found.");
        }
    }

    public void unnoteBones() {
        if (Rs2Widget.getWidget(14352385) == null) {
            if (!Rs2Inventory.isItemSelected()) {
                Rs2Inventory.use("bones");
            } else {
                Rs2Npc.interact("Phials", "Use");
                setSkipTicks(2);
            }
        } else if (Rs2Widget.getWidget(14352385) != null) {
            Rs2Keyboard.keyPress('3');
            setSkipTicks(2);
        }
    }

    private void enterHouse() {
        boolean isAdvertisementWidgetOpen = Rs2Widget.hasWidget("House advertisement");

        if (!isAdvertisementWidgetOpen) {
            Rs2GameObject.interact(ObjectID.HOUSE_ADVERTISEMENT, "View");
            setSkipTicks(2);
            return;
        }

        Widget gildedAlterContainer = Rs2Widget.getWidget(52, 13);
        Widget constLevelContainer = Rs2Widget.getWidget(52, 12);
        if (gildedAlterContainer == null || gildedAlterContainer.getChildren() == null
            || constLevelContainer == null || constLevelContainer.getChildren() == null) return;

        var firstMatch = Arrays.stream(gildedAlterContainer.getChildren())
                .filter(x -> x.getText().equals("Y")
                    && constLevelContainer.getChild(x.getIndex()).getText().equals("99"))
                .findFirst().orElse(null);

        if (firstMatch == null) return;

        Rs2Widget.clickChildWidget(3407891, firstMatch.getIndex());
        setSkipTicks(2);
    }

    public void bonesOnAltar() {
        TileObject altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_40878);
        if (altar == null) {
            altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_13197);
        }
        if (altar != null) {
            Rs2Inventory.useUnNotedItemOnObject("bones", altar);
            setSkipTicks(1);
        }
    }
}
