package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.ArrayList;

public class PriestInPerilTask extends AccountBuilderQuestTask {
    public PriestInPerilTask(){
        super(QuestHelperQuest.PRIEST_IN_PERIL,
                new ItemRequirement("Mithril longsword", ItemID.MITHRIL_LONGSWORD, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemID.LEATHER_GLOVES, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true),
                new ItemRequirement("Amulet of strength", ItemID.AMULET_OF_STRENGTH, 1, true));
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.LARGE_DOOR_3490 && step.getText().stream().anyMatch(x -> x.contains("Return"))){
            if (!Rs2Inventory.contains(ItemID.RUNE_ESSENCE, ItemID.PURE_ESSENCE)){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Bank.walkToBankAndUseBank())
                    return;

                if (Rs2Bank.hasBankItem(ItemID.PURE_ESSENCE, 26))
                    Rs2Bank.withdrawX(ItemID.PURE_ESSENCE, 26);
                else
                    Rs2Bank.withdrawX(ItemID.RUNE_ESSENCE, 26);
            } else if (!isQuestRunning())
                startupQuest();
        } else if (step.objectID == ObjectID.LADDER_17385){
            if (Rs2Player.getWorldLocation().distanceTo(step.getWorldPoint()) < 5)
                Rs2GameObject.interact(ObjectID.LADDER_17385);
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.MONK_OF_ZAMORAK_3486)
            Rs2GroundItem.loot(ItemID.GOLDEN_KEY);
        else if (step.npcID == NpcID.DREZEL
                && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3406, 3481, 10, 16, 2)) == 0
                && !Rs2Dialogue.isInDialogue())
            Rs2Npc.interact(NpcID.DREZEL);
        else if (step.npcID == NpcID.DREZEL && step.getText().stream().anyMatch(x -> x.contains("Bring Drezel"))){
            if (!Rs2Inventory.contains(ItemID.RUNE_ESSENCE, ItemID.PURE_ESSENCE) && !Rs2Dialogue.isInDialogue()){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Bank.walkToBank(BankLocation.VARROCK_EAST) || !Rs2Bank.useBank())
                    return;

                if (Rs2Bank.hasBankItem(ItemID.PURE_ESSENCE, 24))
                    Rs2Bank.withdrawX(ItemID.PURE_ESSENCE, 24);
                else
                    Rs2Bank.withdrawX(ItemID.RUNE_ESSENCE, 24);
            } else if (!isQuestRunning())
                startupQuest();
        }
    }

    ArrayList<GameObject> studiedMonuments = new ArrayList<>();

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Go to the central room, and study the monuments to find which has a key on it."))){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(3423, 9890, 0))
                return;

            var monument = Rs2GameObject.findClosestObjects("Monument", true, 20, new WorldPoint(3423, 9890, 0))
                    .stream().filter(x -> !studiedMonuments.contains(x)).findFirst().orElse(null);

            if (monument == null)
                return;

            Rs2GameObject.interact(monument, "Study");
            sleepUntil(() -> Rs2Widget.isWidgetVisible(272, 0), 10_000);
            sleep(200, 400);
            if (Rs2Widget.getWidget(272, 8).getItemId() == 2945){
                Rs2Inventory.use(ItemID.GOLDEN_KEY);
                Rs2GameObject.interact(monument);
                sleep(1000, 2000);
                startupQuest();
            }
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 30;
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        while (!Rs2Dialogue.hasSelectAnOption()) {
            if (!Rs2Dialogue.isInDialogue())
                Rs2Npc.interact(NpcID.DREZEL);
            else if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();

            sleep(500, 1200);
        }
    }
}
