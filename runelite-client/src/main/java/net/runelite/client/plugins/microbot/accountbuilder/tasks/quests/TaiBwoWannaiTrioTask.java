package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

import java.awt.event.KeyEvent;
import java.util.Arrays;

public class TaiBwoWannaiTrioTask extends AccountBuilderQuestTask {
    public TaiBwoWannaiTrioTask(){
        super(QuestHelperQuest.TAI_BWO_WANNAI_TRIO,
                new ItemRequirement("Ring of wealth(5)", ItemCollections.RING_OF_WEALTHS, 1, true),
                new ItemRequirement("Amulet of glory(6)", ItemID.AMULET_OF_GLORY6, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 4) ,
                new ItemRequirement("Antidote++(4)", ItemID.ANTIDOTE4_5952, 2),
                new ItemRequirement("Stamina potion(4)", ItemID.STAMINA_POTION4, 2),
                new ItemRequirement("Raw karambwan", ItemID.RAW_KARAMBWAN, 4),
                new ItemRequirement("Mithril spear", ItemID.MITHRIL_SPEAR, 1),
                new ItemRequirement("Jogre bones", ItemID.JOGRE_BONES, 1),
                new ItemRequirement("Rune halberd", ItemID.RUNE_HALBERD, 1, true));

        useFood = true;
        useAntiPoison = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Inventory.contains(ItemID.RAW_SHRIMPS))
            Rs2Inventory.drop(ItemID.RAW_SHRIMPS);

        if (currentStep != null && MQuestScript.getFullText(currentStep).toLowerCase().contains("kill") && !Rs2Equipment.isWearing(ItemID.RUNE_HALBERD))
            Rs2Inventory.wear(ItemID.RUNE_HALBERD);
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("sync your current state"))){
            var quests = Arrays.asList(Rs2Widget.getWidget(399, 7).getChildren());
            var questWidget = quests.stream().filter(x -> x.getText().contains(getQuest().getName())).findFirst().orElse(null);
            var index = quests.indexOf(questWidget);

            Rs2Widget.clickWidgetFast(questWidget, index, 2);
        } else if (MQuestScript.getFullText(step).contains("Use a karambwanji on a karambwan Vessel to fill it.")){
            Rs2Inventory.combine(ItemID.RAW_KARAMBWANJI, ItemID.KARAMBWAN_VESSEL);
            Rs2Inventory.waitForInventoryChanges();
        } else if (MQuestScript.getFullText(step).contains("Slice a banana")){
            Rs2GroundItem.loot(ItemID.BANANA);
        } else if (MQuestScript.getFullText(step).contains("Cook the karambwan")){
            Rs2Inventory.useItemOnObject(ItemID.RAW_KARAMBWAN, 26185);
            Rs2Player.waitForWalking();
            sleep(500);
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            Rs2Inventory.waitForInventoryChanges();
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (MQuestScript.getFullText(step).contains("Go east to Musa point to buy some Karamjan rum from Zambo.")){
            if (Rs2Shop.openShop("Zembo"))
                Rs2Shop.buyItem("Karamjan rum", "1");
        } else if (MQuestScript.getFullText(step).contains("Use a poisoned spear")){
            Rs2Inventory.useItemOnNpc(ItemID.MITHRIL_SPEARKP, step.getNpcs().get(0));
            Rs2Inventory.waitForInventoryChanges();
        } else if (MQuestScript.getFullText(step).contains("Go on a hunt with Tamayu.")){
            stopQuest();

            if (!Rs2Dialogue.isInDialogue() && !Rs2Npc.interact("Tamayu", "Talk-to"))
                Rs2Walker.walkTo(step.getWorldPoint());
            else if (Rs2Dialogue.hasSelectAnOption())
                Rs2Widget.clickWidget("Take me on your next hunt for the Shaikahan.");
            else
                Rs2Dialogue.clickContinue();
        } else if (MQuestScript.getFullText(step).contains("Use the vessel on Tinsay.")){
            if (Rs2Dialogue.isInDialogue()) return;

            Rs2Inventory.use("Karambwan vessel");
            Rs2Npc.interact(4701);
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40;
    }

    boolean talkedToTiadeche = false;
    boolean talkedToTamayu = false;
    boolean talkedToTinsay = false;
    boolean finishedDialogue = false;

    @Override
    public void doTaskCleanup(boolean shutdown) {
        while (!talkedToTiadeche || !talkedToTamayu || !talkedToTinsay || !finishedDialogue){
            sleep(1000, 2000);

            if (Rs2Dialogue.isInDialogue()){
                finishedDialogue = false;
                if (!Rs2Dialogue.hasContinue() && Rs2Widget.hasWidget("No."))
                    Rs2Widget.clickWidget("No.");
                else
                    Rs2Dialogue.clickContinue();

                continue;
            }

            finishedDialogue = true;

            if (!talkedToTiadeche){
                if (!Rs2Walker.walkTo(new WorldPoint(2780, 3057, 0)))
                    continue;

                if (Rs2Npc.interact("Tiadeche", "Talk-to") && Global.sleepUntilTrue(() -> Rs2Dialogue.isInDialogue(), 10, 5000))
                    talkedToTiadeche = true;

                finishedDialogue = false;
                continue;
            }

            if (!talkedToTamayu){
                if (!Rs2Walker.walkTo(new WorldPoint(2800, 3057, 0)))
                    continue;

                if (Rs2Npc.interact("Tamayu", "Talk-to") && Global.sleepUntilTrue(() -> Rs2Dialogue.isInDialogue(), 10, 5000))
                    talkedToTamayu = true;

                finishedDialogue = false;
                continue;
            }

            if (!talkedToTinsay){
                if (!Rs2Walker.walkTo(new WorldPoint(2790, 3054, 0)))
                    continue;

                if (Rs2Npc.interact("Tinsay", "Talk-to") && Global.sleepUntilTrue(() -> Rs2Dialogue.isInDialogue(), 10, 5000))
                    talkedToTinsay = true;

                finishedDialogue = false;
            }
        }

        super.doTaskCleanup(shutdown);
    }
}
