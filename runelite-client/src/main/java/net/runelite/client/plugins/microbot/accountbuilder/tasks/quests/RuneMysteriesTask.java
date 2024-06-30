package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class RuneMysteriesTask extends AccountBuilderQuestTask {
    public RuneMysteriesTask(){
        super(QuestHelperQuest.RUNE_MYSTERIES);
        memberOnly = false;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 12;
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Dialogue.isInDialogue() && Rs2Widget.hasWidget("Climb down"))
            Rs2Widget.clickWidget("Climb down");
    }
}
