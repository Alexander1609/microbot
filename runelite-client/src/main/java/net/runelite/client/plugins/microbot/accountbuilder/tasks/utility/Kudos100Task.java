package net.runelite.client.plugins.microbot.accountbuilder.tasks.utility;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

import java.util.List;

public class Kudos100Task extends AccountBuilderTask {
    public Kudos100Task(){
        addRequirement(ItemID.LEATHER_GLOVES, true);
        addRequirement(ItemID.LEATHER_BOOTS, true);
        addRequirement(ItemID.TROWEL, 1);
    }

    @Override
    public String getName() {
        return "Utility: Varrock Museum Kudos (100+)";
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getVarbitValue(3637) >= 28
                && Microbot.getVarbitValue(3637) < 100
                && Microbot.getClient().getRealSkillLevel(Skill.SLAYER) >= 20
                && isQuestCompleted(QuestHelperQuest.RUNE_MYSTERIES)
                && isQuestCompleted(QuestHelperQuest.MERLINS_CRYSTAL)
                && isQuestCompleted(QuestHelperQuest.PRIEST_IN_PERIL)
                && isQuestCompleted(QuestHelperQuest.THE_GRAND_TREE)
                && isQuestCompleted(QuestHelperQuest.HAZEEL_CULT);
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || Microbot.getVarbitValue(3637) >= 103;
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    List<Integer> itemsToSearch = List.of(ItemID.OLD_SYMBOL, ItemID.ANCIENT_SYMBOL, ItemID.OLD_COIN, ItemID.ANCIENT_COIN, ItemID.POTTERY, ItemID.CLEAN_NECKLACE);
    boolean foundAllItems = false;
    boolean talkedTo = false;

    @Override
    public void tick() {
        if (Rs2Dialogue.isInDialogue()){
            if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();
            else if (Rs2Widget.hasWidget("I have some information which might be of use in your displays."))
                Rs2Widget.clickWidget("I have some information which might be of use in your displays.");
            else if (Rs2Dialogue.hasSelectAnOption())
                Rs2Keyboard.keyPress('1');

            return;
        }

        if (Microbot.getVarbitValue(3637) < 78){
            if (foundAllItems){
                if (Rs2Inventory.contains(ItemID.CLEAN_NECKLACE)){
                    if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                        return;

                    Rs2Inventory.useItemOnNpc(ItemID.CLEAN_NECKLACE, 1906);
                    sleepUntil(Rs2Dialogue::isInDialogue, 5000);
                    return;
                }

                if (Rs2Inventory.contains(ItemID.POTTERY)){
                    if (!talkedTo){
                        if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                            return;

                        Rs2Inventory.useItemOnNpc(ItemID.POTTERY, 1906);
                        if (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 5000))
                            talkedTo = true;
                        return;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(3260, 3450, 0)))
                        return;

                    Rs2Inventory.useItemOnObject(ItemID.POTTERY, 12139);
                    Rs2Inventory.waitForInventoryChanges();
                    if (!Rs2Inventory.contains(ItemID.POTTERY))
                        talkedTo = false;

                    return;
                }

                if (Rs2Inventory.contains(ItemID.OLD_SYMBOL)){
                    if (!talkedTo){
                        if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                            return;

                        Rs2Inventory.useItemOnNpc(ItemID.OLD_SYMBOL, 1906);
                        if (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 5000))
                            talkedTo = true;
                        return;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(3260, 3450, 0)))
                        return;

                    Rs2Inventory.useItemOnObject(ItemID.OLD_SYMBOL, 12137);
                    Rs2Inventory.waitForInventoryChanges();
                    if (!Rs2Inventory.contains(ItemID.OLD_SYMBOL))
                        talkedTo = false;

                    return;
                }

                if (Rs2Inventory.contains(ItemID.ANCIENT_SYMBOL)){
                    if (!talkedTo){
                        if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                            return;

                        Rs2Inventory.useItemOnNpc(ItemID.ANCIENT_SYMBOL, 1906);
                        if (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 5000))
                            talkedTo = true;
                        return;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(3260, 3450, 0)))
                        return;

                    Rs2Inventory.useItemOnObject(ItemID.ANCIENT_SYMBOL, 12138);
                    Rs2Inventory.waitForInventoryChanges();
                    if (!Rs2Inventory.contains(ItemID.ANCIENT_SYMBOL))
                        talkedTo = false;

                    return;
                }

                if (Rs2Inventory.contains(ItemID.ANCIENT_COIN)){
                    if (!talkedTo){
                        if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                            return;

                        Rs2Inventory.useItemOnNpc(ItemID.ANCIENT_COIN, 1906);
                        if (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 5000))
                            talkedTo = true;
                        return;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(3260, 3450, 0)))
                        return;

                    Rs2Inventory.useItemOnObject(ItemID.ANCIENT_COIN, 12234);
                    Rs2Inventory.waitForInventoryChanges();
                    if (!Rs2Inventory.contains(ItemID.ANCIENT_COIN))
                        talkedTo = false;

                    return;
                }

                if (Rs2Inventory.contains(ItemID.OLD_COIN)){
                    if (!talkedTo){
                        if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                            return;

                        Rs2Inventory.useItemOnNpc(ItemID.OLD_COIN, 1906);
                        if (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 5000))
                            talkedTo = true;
                        return;
                    }

                    if (!Rs2Walker.walkTo(new WorldPoint(3260, 3450, 0)))
                        return;

                    Rs2Inventory.useItemOnObject(ItemID.OLD_COIN, 15484);
                    Rs2Inventory.waitForInventoryChanges();
                    if (!Rs2Inventory.contains(ItemID.OLD_COIN))
                        talkedTo = false;

                    return;
                }

                return;
            }

            if (!Rs2Walker.walkTo(new WorldPoint(3260, 3444, 0)))
                return;

            if (!Rs2Inventory.contains(ItemID.SPECIMEN_BRUSH) || !Rs2Inventory.contains(ItemID.ROCK_PICK)){
                Rs2GameObject.interact(24535, "Take");
                Rs2Player.waitForWalking();
                return;
            }

            Rs2Inventory.dropAll(
                    ItemID.JEWELLERY,
                    ItemID.OLD_CHIPPED_VASE,
                    ItemID.ARROWHEADS,
                    ItemID.BROKEN_GLASS,
                    ItemID.BROKEN_GLASS_1469,
                    ItemID.BROKEN_ARROW,
                    ItemID.IRON_DAGGER,
                    ItemID.BONES,
                    ItemID.UNCUT_JADE);

            for (var item : itemsToSearch){
                while (Rs2Inventory.count(item) > 1) {
                    Rs2Inventory.drop(item);
                    Rs2Inventory.waitForInventoryChanges();
                }
            }

            if (itemsToSearch.stream().allMatch(Rs2Inventory::contains))
                foundAllItems = true;
            else {
                if (!Rs2Inventory.contains(ItemID.UNCLEANED_FIND)){
                    Rs2GameObject.interact(24557);
                    Rs2Player.waitForAnimation();
                    while (Global.sleepUntilTrue(Rs2Player::isAnimating, 10, 2000))
                        sleep(1000);
                } else {
                    Rs2GameObject.interact(24556);
                    Rs2Player.waitForAnimation();
                    while (Global.sleepUntilTrue(Rs2Player::isAnimating, 10, 2000))
                        sleep(1000);
                }
            }
        } else {
            if (!Rs2Walker.walkTo(new WorldPoint(3266, 3455, 1)))
                return;

            Rs2Npc.interact(NpcID.HISTORIAN_MINAS);
            sleepUntil(Rs2Dialogue::isInDialogue, 5000);
        }
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        while (Rs2Inventory.contains("Antique lamp")){
            Rs2Tab.switchToInventoryTab();
            sleep(500, 1000);
            Rs2Inventory.interact("Antique lamp", "Rub");
            sleepUntil(() -> Rs2Widget.isWidgetVisible(240, 0), 5000);
            Rs2Widget.clickWidget(240, 14); // Slayer
            sleep(500, 1000);
            Rs2Widget.clickWidget(240, 26);
            sleep(500, 1000);
        }

        while (!Rs2Walker.walkTo(new WorldPoint(3254, 3454, 0)))
            sleep(500);

        while (!Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 2000)) {
            Rs2Npc.interact(NpcID.INFORMATION_CLERK);
            sleepUntil(Rs2Dialogue::isInDialogue, 5000);
        }

        while (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 2000)) {
            if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();
            else if (Rs2Dialogue.hasSelectAnOption())
                Rs2Keyboard.keyPress('1');

            sleep(1000);
        }
    }
}
