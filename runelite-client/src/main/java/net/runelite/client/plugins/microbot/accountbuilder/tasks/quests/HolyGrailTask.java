package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class HolyGrailTask extends AccountBuilderQuestTask {
    boolean talkedToFisherman = false;

    public HolyGrailTask(){
        super(QuestHelperQuest.HOLY_GRAIL,
                new ItemRequirement("Rune halberd", ItemID.RUNE_HALBERD, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemID.LEATHER_GLOVES, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true),
                new ItemRequirement("Amulet of strength", ItemID.AMULET_OF_STRENGTH, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10),
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true),
                new ItemRequirement("Camelot teleport", ItemID.CAMELOT_TELEPORT, 3));

        useFood = true;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.MONK_OF_ENTRANA_1167){
            if (Rs2Equipment.isWearing(ItemID.RUNE_HALBERD)){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Bank.walkToBankAndUseBank())
                    return;

                Rs2Bank.depositItems(ItemID.EXCALIBUR);
                sleep(500, 800);
                Rs2Bank.depositEquipment();
            } else if (!isQuestRunning())
                startupQuest();
        } else if (step.npcID == NpcID.BLACK_KNIGHT_TITAN){
            var health = step.getNpcs().get(0).getHealthRatio();
            if (health > -1 && health < 2 && !Rs2Equipment.isWearing(ItemID.EXCALIBUR))
                Rs2Inventory.wield(ItemID.EXCALIBUR);
        } else if (step.npcID == NpcID.FISHERMAN_4065){
            if (Rs2Dialogue.isInDialogue() && !talkedToFisherman){
                if (isQuestRunning())
                    stopQuest();

                if (Rs2Dialogue.hasContinue())
                    Rs2Dialogue.clickContinue();
                else
                    Rs2Widget.clickWidget("Any idea how to get into the castle?");
            } else if (!isQuestRunning()) {
                startupQuest();
                talkedToFisherman = true;
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Whistles")))
            Rs2GroundItem.loot(ItemID.MAGIC_WHISTLE);
        else if (step.getText().stream().anyMatch(x -> x.contains("retrieve Excalibur"))){
            withdrawBuyItems();
        } else if (step.getText().stream().anyMatch(x -> x.contains("Pickup the bell outside of the castle."))){
            if (Rs2GroundItem.loot(ItemID.GRAIL_BELL))
                Rs2Inventory.waitForInventoryChanges();
        } else if (step.getText().stream().anyMatch(x -> x.contains("Pickup the Grail.")))
            if (Rs2GroundItem.loot(ItemID.HOLY_GRAIL))
                Rs2Inventory.waitForInventoryChanges();
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.LARGE_DOOR_1524){
            Rs2Walker.walkTo(new WorldPoint(2633, 4689, 0));
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45;
    }
}
