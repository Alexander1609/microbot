package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class APorcineOfInterestTask extends AccountBuilderQuestTask {
    public APorcineOfInterestTask(){
        super(QuestHelperQuest.A_PORCINE_OF_INTEREST,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10));

        useFood = true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == NullObjectID.NULL_40350){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3148, 9705, 21, 15, 0)) == 0){
                Rs2GameObject.interact(40331);
                Rs2Player.waitForAnimation();
            }
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.SOURHOG || step.npcID == NpcID.SOURHOG_10436){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3148, 9705, 21, 15, 0)) == 0){
                Rs2GameObject.interact(40331);
                Rs2Player.waitForAnimation();
            }
        } else if (step.npcID == NpcID.SARAH){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3148, 9705, 21, 15, 0)) == 0){
                Rs2GameObject.interact(40330);
                Rs2Player.waitForAnimation();
            } else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3149, 9696, 29, 8, 0)) == 0){
                Rs2GameObject.interact(40331);
                Rs2Player.waitForAnimation();
            }
        }
    }
}
