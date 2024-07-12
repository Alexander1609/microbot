package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeSlots;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TheGiantDwarfTask extends AccountBuilderQuestTask {
    public TheGiantDwarfTask(){
        super(QuestHelperQuest.THE_GIANT_DWARF,
                new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF),
                new ItemRequirement("Ardy cloak", ItemCollections.ARDY_CLOAKS, 1, true),
                new ItemRequirement("Coins", ItemCollections.COINS, 50_000));

        taskItemNames = taskItems.stream().collect(Collectors.toMap(x -> x, x -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(x).getName())));
        directorTaskItemNames = directorTaskItems.stream().collect(Collectors.toMap(x -> x, x -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(x).getName())));
    }

    List<Integer> taskItems = List.of(
            ItemID.CLAY,
            ItemID.COPPER_ORE,
            ItemID.TIN_ORE,
            ItemID.IRON_ORE,
            ItemID.SILVER_ORE,
            ItemID.GOLD_ORE,
            ItemID.MITHRIL_ORE,
            ItemID.COAL);

    List<Integer> directorTaskItems = List.of(
            ItemID.BRONZE_BAR,
            ItemID.IRON_BAR,
            ItemID.SILVER_BAR,
            ItemID.GOLD_BAR,
            ItemID.STEEL_BAR,
            ItemID.MITHRIL_BAR);

    Map<Integer, String> taskItemNames;
    Map<Integer, String> directorTaskItemNames;

    Integer itemToBuy = null;
    int amountToBuy = 0;

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Take the Left boot")){
            blockStuckPrevention = true;
            Rs2Walker.walkFastCanvas(new WorldPoint(2837, 10220, 0));
            Rs2Player.waitForWalking();
            Rs2GameObject.interact(6111, "Close");
            var dromund = Rs2Npc.getNpc(NpcID.DROMUND);
            sleepUntil(() -> dromund.getCurrentOrientation() == 512, 10_000);
            sleepUntil(() -> dromund.getCurrentOrientation() == 1024, 10_000);
            Rs2GroundItem.loot(ItemID.LEFT_BOOT);
            Rs2Inventory.waitForInventoryChanges();
        } else if (MQuestScript.getFullText(step).contains("Take the Right boot")) {
            blockStuckPrevention = true;
            var target = new WorldPoint(2836, 10227, 0);
            Rs2Walker.walkTo(target.dy(2));
            Rs2Player.waitForWalking();
            sleep(1000);
            Rs2Walker.walkFastCanvas(target);
            sleep(50, 100);
            Rs2Magic.cast(MagicAction.TELEKINETIC_GRAB);
            Rs2Player.waitForWalking();
            var dromund = Rs2Npc.getNpc(NpcID.DROMUND);
            sleepUntil(() -> dromund.getCurrentOrientation() == 1536, 10_000);
            sleepUntil(() -> dromund.getCurrentOrientation() == 0, 10_000);
            Rs2GroundItem.loot(ItemID.RIGHT_BOOT);
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (MQuestScript.getFullText(step).contains("Keep talking to the same secretary")){
            minTickTime = 20;
            maxTickTime = 20;
            var widget = Rs2Widget.getWidget(231, 6);
            if (widget == null) return;

            var text = widget.getText().toLowerCase().replace("<br>", " ");
            // Text	We need 5 pieces of mithril ore in the next 15 minutes.
            // Text	Have you completed your task yet? Did you get our 5<br>pieces of mithril ore?
            int amount = 0;
            if (text.contains("pieces of")){
                var piecesOfIndex = text.indexOf("pieces of");
                amount = Integer.parseInt(text.substring(piecesOfIndex - 2, piecesOfIndex - 1));
            } else if (text.contains("piece of")){
                amount = 1;
            }

            if (amount == 0) return;
            sleep(2000);

            itemToBuy = taskItemNames.entrySet().stream().filter(x -> text.contains(x.getValue().toLowerCase())).findFirst().get().getKey();
            amountToBuy = amount;
        } else if (MQuestScript.getFullText(step).contains("Keep talking to the director")){
            minTickTime = 20;
            maxTickTime = 20;
            var widget = Rs2Widget.getWidget(231, 6);
            if (widget == null) return;

            var text = widget.getText().toLowerCase().replace("<br>", " ");
            int amount = 0;
            if (text.contains("bars of")){
                var piecesOfIndex = text.indexOf("bars of");
                try {
                    amount = Integer.parseInt(text.substring(piecesOfIndex - 2, piecesOfIndex - 1));
                } catch (Exception e) {
                    amount = 5;
                }
            }

            if (amount == 0) return;
            sleep(2000);

            itemToBuy = directorTaskItemNames.entrySet().stream().filter(x -> text.contains(x.getValue().toLowerCase().replace(" bar", ""))).findFirst().get().getKey();
            amountToBuy = amount;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (itemToBuy != null){
            stopQuest();

            if (Rs2Bank.hasBankItem(itemToBuy, amountToBuy)){
                if (!Rs2Bank.walkToBankAndUseBank())
                    return;

                Rs2Bank.depositAll(x -> x.name.contains("bar"));
                Rs2Bank.withdrawX(itemToBuy, amountToBuy);
                itemToBuy = null;
            } else {
                if (!Rs2GrandExchange.walkToGrandExchange() || !Rs2GrandExchange.openExchange())
                    return;

                var minClicks = 2;
                var clicks = 4;
                boolean bought = false;
                while (!bought && !canceled){
                    if (itemToBuy == null) return;

                    var itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(itemToBuy));

                    Rs2GrandExchange.buyItemAboveXPercent(itemComposition.getName(), amountToBuy, minClicks, clicks);
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

                if (bought) {
                    Rs2Bank.openBank();
                    sleepUntil(() -> Rs2Bank.isOpen(), 2000);
                    Rs2Bank.depositAll(x -> x.name.contains("bar"));
                    Rs2Bank.withdrawX(itemToBuy, amountToBuy);
                    itemToBuy = null;
                }
            }
        } else if (!isQuestRunning())
            startupQuest();
    }
}
