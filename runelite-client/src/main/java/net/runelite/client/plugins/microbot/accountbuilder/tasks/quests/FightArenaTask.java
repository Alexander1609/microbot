package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class FightArenaTask extends AccountBuilderQuestTask {
    public FightArenaTask(){
        super(QuestHelperQuest.FIGHT_ARENA,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemID.LEATHER_GLOVES, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true),
                new ItemRequirement("Amulet of strength", ItemID.AMULET_OF_STRENGTH, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 20));

        useFood = true;
        usePrayerFlicking = true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == 46563){
            Rs2GameObject.interact(step.objectID, "Quick-escape");
            Rs2Player.waitForWalking();
        }
    }
}
