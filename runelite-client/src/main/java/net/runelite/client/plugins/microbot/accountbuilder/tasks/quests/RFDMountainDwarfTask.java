package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class RFDMountainDwarfTask extends AccountBuilderQuestTask {
    public RFDMountainDwarfTask(){
        super(QuestHelperQuest.RECIPE_FOR_DISASTER_DWARF,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Adamant kiteshield", ItemID.ADAMANT_KITESHIELD, 1, true));
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        while (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1855, 5312, 17, 47, 0)) == 0){
            Rs2GameObject.interact("Barrier", "Pass-trough");
            Rs2Player.waitForWalking();
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.ICEFIEND && !Rs2Combat.inCombat()){
            Rs2Npc.attack(NpcID.ICEFIEND_4813);
            Rs2Player.waitForAnimation();
        }
    }
}
