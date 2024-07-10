package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeSlots;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.*;
import java.util.stream.Collectors;

public class FairytaleITask extends AccountBuilderQuestTask {
    static List<Integer> itemList = Arrays.asList(
            ItemID.WHITE_BERRIES,
            ItemID.MORT_MYRE_PEAR,
            ItemID.MORT_MYRE_STEM,
            ItemID.MORT_MYRE_FUNGUS,
            ItemID.NATURE_TALISMAN,
            ItemID.AVANTOE,
            ItemID.IRIT_LEAF,
            ItemID.BLUE_DRAGON_SCALE,
            ItemID.PROBOSCIS,
            ItemID.JANGERBERRIES,
            ItemID.POTATO_CACTUS,
            ItemID.SNAPDRAGON,
            ItemID.SUPERCOMPOST,
            ItemID.BABYDRAGON_BONES,
            ItemID.UNCUT_DIAMOND,
            ItemID.RAW_CAVE_EEL,
            ItemID.EDIBLE_SEAWEED,
            ItemID.OYSTER,
            ItemID.CHARCOAL,
            ItemID.FAT_SNAIL,
            ItemID.RED_SPIDERS_EGGS,
            ItemID.RAW_SLIMY_EEL,
            ItemID.GRAPES,
            ItemID.UNCUT_RUBY,
            ItemID.JOGRE_BONES,
            ItemID.KING_WORM,
            ItemID.SNAPE_GRASS,
            ItemID.LIME
    );

    public FairytaleITask(){
        super(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS,
                new ItemRequirement("Lumbridge teleport", ItemID.LUMBRIDGE_TELEPORT, 3));

        followUpTask = new FairytaleIIUnlockTask();
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Rs2Bank.hasBankItem("Coins", 50_000);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("Enter the swamp."))
            Rs2Widget.clickWidget("Enter the swamp.");
    }

    Map<Integer, WorldPoint> farmerLocations = Map.of(
            NpcID.FRIZZY_SKERNIP, new WorldPoint(3060, 3258, 0),
            NpcID.HESKEL, new WorldPoint(3003, 3373, 0),
            NpcID.DREVEN, new WorldPoint(3182, 3357, 0),
            NpcID.TREZNOR_11957, new WorldPoint(3229, 3459, 0),
            NpcID.ELSTAN, new WorldPoint(3055, 3307, 0));

    Set<Integer> talkedToFarmers = new HashSet<>();

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Talk to 5 farmers"))){
            if (!Rs2Dialogue.isInDialogue() && talkedToFarmers.size() < 5 && (step.getNpcs().isEmpty() || talkedToFarmers.contains(step.getNpcs().get(0).getId()))){
                if (Global.sleepUntilTrue(Rs2Dialogue::isInDialogue, 10, 500))
                    return;

                if (isQuestRunning())
                    stopQuest();

                var nextLocation = farmerLocations.entrySet().stream().filter(x -> !talkedToFarmers.contains(x.getKey()))
                        .min(Comparator.comparing(x -> x.getValue().distanceTo(Rs2Player.getWorldLocation()))).orElse(null);
                if (nextLocation != null && Rs2Walker.walkTo(nextLocation.getValue()) && !isQuestRunning())
                    startupQuest();
            } else if (Rs2Dialogue.isInDialogue() && !step.getNpcs().isEmpty() && step.getNpcs().get(0).getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 2)
                talkedToFarmers.add(step.getNpcs().get(0).getId());
            else if (!isQuestRunning())
                startupQuest();
        } else if (step.npcID == NpcID.TANGLEFOOT){
            if (isQuestRunning()) {
                stopQuest();
                blockStuckPrevention = true;
            }

            if (!Rs2Equipment.isWearing(ItemID.MAGIC_SECATEURS))
                Rs2Inventory.equip(ItemID.MAGIC_SECATEURS);

            if (!Rs2Walker.walkTo(new WorldPoint(2375, 4390, 0), 10))
                return;

            var npcInstLoc = step.getNpcs().get(0).getWorldLocation();
            var npcLocal = LocalPoint.fromWorld(Microbot.getClient(), npcInstLoc);
            var npcLoc = WorldPoint.fromLocalInstance(Microbot.getClient(), npcLocal);
            if (!npcLoc.equals(new WorldPoint(2377, 4389, 0))){
                if (Microbot.getClient().getEnergy() < 2000) {
                    sleepUntil(() -> Microbot.getClient().getEnergy() > 3000, 60_000);
                    return;
                }

                if (npcLoc.equals(new WorldPoint(2378, 4387, 0)) || npcLoc.distanceTo(new WorldArea(2375, 4378, 6, 9, 0)) == 0){
                    Rs2Npc.interact(NpcID.TANGLEFOOT, "Attack");
                    Rs2Player.waitForAnimation();
                    Rs2Walker.walkFastCanvas(new WorldPoint(2378, 4390, 0));
                    Rs2Player.waitForWalking();
                }
            } else {
                Rs2Npc.interact(NpcID.TANGLEFOOT, "Attack");
                var tick = Microbot.getClient().getTickCount();
                sleepUntil(() -> Microbot.getClient().getTickCount() > tick, 1000);
                Rs2Walker.walkFastCanvas(new WorldPoint(2378, 4390, 0));
                Rs2Player.waitForWalking();
                sleep(4000);
            }

            /*if (Microbot.getClient().getEnergy() < 2000)
                return;

            Rs2Npc.interact(NpcID.TANGLEFOOT, "Attack");
            Rs2Player.waitForAnimation();
            Rs2Walker.walkFastCanvas(new WorldPoint(2371, 4391, 0));
            Rs2Player.waitForWalking();*/
        }
    }

    List<Integer> itemsToBuy = new ArrayList<>();

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.GROTTO && (itemsToBuy.isEmpty() || itemsToBuy.stream().anyMatch(x -> !itemsBought.contains(x)))){
            if (isQuestRunning())
                stopQuest();

            if (!itemsToBuy.isEmpty()){
                if (!Rs2GrandExchange.walkToGrandExchange() || !Rs2GrandExchange.openExchange())
                    return;

                var minClicks = 2;
                var clicks = 4;
                boolean bought = false;
                while (!bought && !canceled){
                    Integer itemToBuy = itemsToBuy.stream().filter(x -> !itemsBought.contains(x)).findFirst().orElse(null);
                    if (itemToBuy == null) return;

                    var itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(itemToBuy));

                    Rs2GrandExchange.buyItemAboveXPercent(itemComposition.getName(), 1, minClicks, clicks);
                    sleepUntil(() -> Rs2GrandExchange.hasBoughtOffer(itemToBuy), 10000);

                    if (Rs2GrandExchange.hasBoughtOffer(itemToBuy)){
                        Rs2GrandExchange.collectToInventory();
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
            } else if (!Rs2Widget.isWidgetVisible(WidgetInfo.DIARY_QUEST_WIDGET_TEXT)){
                var quests = Arrays.asList(Rs2Widget.getWidget(399, 7).getChildren());
                var questWidget = quests.stream().filter(x -> x.getText().contains(getQuest().getName())).findFirst().orElse(null);
                var index = quests.indexOf(questWidget);

                Rs2Widget.clickWidgetFast(questWidget, index, 2);
            } else {
                var textWidget = Rs2Widget.getWidget(WidgetInfo.DIARY_QUEST_WIDGET_TEXT);
                var combinedText = String.join("", Arrays.stream(textWidget.getStaticChildren()).map(Widget::getText).collect(Collectors.toList())).toLowerCase().replace("<col=800000>", "");
                var itemDefs = itemList.stream().map(x -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(x))).collect(Collectors.toList());

                itemsToBuy = itemDefs.stream().filter(x -> combinedText.contains(x.getName().toLowerCase())).map(ItemComposition::getId).collect(Collectors.toList());
            }
        } else if (!isQuestRunning())
            startupQuest();
    }
}
