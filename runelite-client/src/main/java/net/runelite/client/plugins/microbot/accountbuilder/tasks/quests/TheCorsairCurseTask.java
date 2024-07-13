package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class TheCorsairCurseTask extends AccountBuilderQuestTask {
    public TheCorsairCurseTask(){
        super(QuestHelperQuest.THE_CORSAIR_CURSE,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10));

        useFood = true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.GNOCCI_THE_COOK){
            if (Rs2Dialogue.hasContinue() && Rs2Widget.hasWidget("I'm still looking into it.")){
                stopQuest();

                while (!Rs2Walker.walkTo(2554, 2857, 1))
                    sleep(500);

                while (!Rs2Dialogue.isInDialogue()){
                    Rs2Npc.interact(NpcID.ARSEN_THE_THIEF);
                    Rs2Player.waitForWalking();
                    sleep(500);
                }

                if (Rs2Dialogue.isInDialogue())
                    startupQuest();
            }
        }
    }
}
