package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class AlfredGrimhandsBarcrawlTask extends AccountBuilderQuestTask {
    public AlfredGrimhandsBarcrawlTask(){
        super(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL,
                new ItemRequirement("Stam", ItemID.STAMINA_POTION4, 2),
                new ItemRequirement("Energy", ItemID.ENERGY_POTION4, 4));
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        sleep(500);
        while (Rs2Dialogue.isInDialogue()){
            if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();
            else if (Rs2Dialogue.hasSelectAnOption())
                Rs2Widget.clickWidget("Yes please, I want to smash my vials.");

            sleep(500, 1000);
        }
    }
}
