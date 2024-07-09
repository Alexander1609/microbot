package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class MerlinsCrystalTask extends AccountBuilderQuestTask {
    public MerlinsCrystalTask(){
        super(QuestHelperQuest.MERLINS_CRYSTAL,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemID.LEATHER_GLOVES, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true),
                new ItemRequirement("Amulet of strength", ItemID.AMULET_OF_STRENGTH, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10),
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true),
                new ItemRequirement("Bread", ItemID.BREAD, 2));

        useFood = true;
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Return to Catherby.")))
            Rs2Walker.walkTo(new WorldPoint(2806, 3435, 0));
        else if (step.getText().stream().anyMatch(x -> x.contains("Go stand in the star symbol"))
                && Rs2Player.getWorldLocation().distanceTo(step.getWorldPoint()) < 5
                && Rs2Player.getWorldLocation().distanceTo(step.getWorldPoint()) > 0)
            Rs2Walker.walkFastCanvas(step.getWorldPoint());
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.BEGGAR && Rs2Dialogue.isInDialogue()){
            if (isQuestRunning())
                stopQuest();

            if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();
            else if (Rs2Dialogue.hasSelectAnOption())
                Rs2Widget.clickWidget("Yes certainly.");
        } else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45;
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
