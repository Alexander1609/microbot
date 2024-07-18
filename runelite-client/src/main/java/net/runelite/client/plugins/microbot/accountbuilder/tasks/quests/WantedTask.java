package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class WantedTask extends AccountBuilderQuestTask {
    public WantedTask(){
        super(QuestHelperQuest.WANTED,
                new ItemRequirement("", ItemCollections.AMULET_OF_GLORIES, 1, true),
                new ItemRequirement("", ItemCollections.ARDY_CLOAKS, 1, true),
                new ItemRequirement("", ItemCollections.COMBAT_BRACELETS, 1, true),
                new ItemRequirement("", ItemID.WARRIOR_RING, 1, true),
                new ItemRequirement("", ItemID.RUNE_BOOTS, 1, true),
                new ItemRequirement("", ItemID.RUNE_PLATELEGS, 1, true),
                new ItemRequirement("", ItemID.RUNE_KITESHIELD, 1, true),
                new ItemRequirement("", ItemID.RUNE_CHAINBODY, 1, true),
                new ItemRequirement("", ItemID.RUNE_FULL_HELM, 1, true),
                new ItemRequirement("", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF),
                new ItemRequirement("Kandarin headgears", ItemCollections.KANDARIN_HEADGEARS));
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 45;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.BLACK_KNIGHT){
            if (Rs2Combat.inCombat()){
                stopQuest();

                sleepUntil(() -> !Rs2Combat.inCombat(), 5000);
                startupQuest();
            }
        }
    }
}
