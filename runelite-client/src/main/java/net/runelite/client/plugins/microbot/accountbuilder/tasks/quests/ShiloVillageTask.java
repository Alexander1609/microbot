package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.JewelleryLocationEnum;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class ShiloVillageTask extends AccountBuilderQuestTask {

    boolean hasUsedKaramjaTp = false;

    public ShiloVillageTask() {
        super(QuestHelperQuest.SHILO_VILLAGE, new ItemRequirement("Ring of wealth(5)", ItemID.RING_OF_WEALTH_5, 1, true),
                new ItemRequirement("Amulet of glory(6)", ItemID.AMULET_OF_GLORY6, 1, true), new ItemRequirement("Potato with cheese", ItemID.POTATO_WITH_CHEESE, 15), new ItemRequirement("Stamina potion(4)", ItemID.STAMINA_POTION4, 2), new ItemRequirement("Antidote++(4)", ItemID.ANTIDOTE4_5952, 1),
                new ItemRequirement("Spade", ItemID.SPADE, 1),
                new ItemRequirement("Lit candle", ItemID.LIT_CANDLE, 1),
                new ItemRequirement("Rope", ItemID.ROPE, 1),
                new ItemRequirement("Bronze wire", ItemID.BRONZE_WIRE, 1),
                new ItemRequirement("Chisel", ItemID.CHISEL, 1),
                new ItemRequirement("Bones", ItemID.BONES, 3)
        );
        useFood = true;
        useAntiPoison = true;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
    }


    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Bury Zadimus's")) {
            stopQuest();
            if (!hasUsedKaramjaTp && step.getWorldPoint().distanceTo(Rs2Player.getWorldLocation()) > 5) {
                Rs2Equipment.useAmuletAction(JewelleryLocationEnum.KARAMJA);
                hasUsedKaramjaTp = true;
                Rs2Player.waitForAnimation();
            }
            if (step.getWorldPoint().distanceTo(Rs2Player.getWorldLocation()) > 3) {
                Rs2Walker.walkTo(step.getWorldPoint());
            } else if (!Rs2Dialogue.isInDialogue()) {
                Rs2Inventory.interact(ItemID.ZADIMUS_CORPSE, "Bury");
                Rs2Player.waitForAnimation();
                hasUsedKaramjaTp = false;
            } else {
                startupQuest();
            }
        } else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {

        if (step.objectID == 2246){
            GameObject object = Rs2GameObject.findObject(2257, new WorldPoint(2928, 9515, 0));
            if (object!=null && object.getWorldLocation().distanceTo(Rs2Player.getWorldLocation())<=2) {
                Rs2GameObject.interact(object, "Climb");
                Rs2Player.waitForAnimation();
            }else if (step.getWorldPoint().distanceTo(Rs2Player.getWorldLocation())>5){
                Rs2Walker.walkTo(step.getWorldPoint());
            }
        } else if (step.objectID == 2234) {
            //gets stuck on walking up the stones
            if (Rs2Player.getWorldLocation().equals(new WorldPoint(2794, 2977, 0)) && !Rs2Player.isWalking()) {
                Rs2Walker.walkCanvas(new WorldPoint(2795, 2979, 0));
                sleep(600);
            } else if (Rs2Player.getWorldLocation().getRegionID() ==11668 && MQuestScript.getFullText(step).contains("Right-click")){
                stopQuest();
                if (Rs2Inventory.hasItem("Amulet of glory(")) {
                    Rs2Inventory.wear("Amulet of glory(");
                } else if (Rs2Equipment.isWearing("Amulet of glory(")) {
                    Rs2Equipment.useAmuletAction(JewelleryLocationEnum.KARAMJA);
                    Rs2Player.waitForAnimation();
                } else if (step.getWorldPoint().distanceTo(Rs2Player.getWorldLocation()) > 3) {
                    Rs2Walker.walkTo(step.getWorldPoint());
                } else if (!isQuestRunning())
                    startupQuest();
            }
        } else if (step.objectID == 2237 && !hasUsedKaramjaTp) {
            stopQuest();
            if (Rs2Inventory.hasItem("Amulet of glory(")) {
                Rs2Inventory.wear("Amulet of glory(");
            } else if (Rs2Equipment.isWearing("Amulet of glory(")) {
                Rs2Equipment.useAmuletAction(JewelleryLocationEnum.KARAMJA);
                Rs2Player.waitForAnimation();
                startupQuest();
            }
        } else if (step.objectID == 5353){
            //fighting stop interacting each time with the monster
            if (Rs2Player.isInteracting()) {
                stopQuest();
            } else {
                startupQuest();
            }
        }else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) > 35;
    }
}
