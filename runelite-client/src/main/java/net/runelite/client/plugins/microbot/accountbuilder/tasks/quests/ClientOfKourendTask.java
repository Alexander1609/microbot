package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;

public class ClientOfKourendTask extends AccountBuilderQuestTask {
    public ClientOfKourendTask(){
        super(QuestHelperQuest.CLIENT_OF_KOUREND);
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (MQuestScript.getFullText(step).contains("Activate the mysterious orb at the Dark Altar.")){
            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                stopQuest();
            else
                startupQuest();
        }
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        Rs2Tab.switchToInventoryTab();
        sleep(500, 1000);
        Rs2Inventory.interact("Antique lamp", "Rub");
        sleepUntil(() -> Rs2Widget.isWidgetVisible(240, 0), 5000);
        Rs2Widget.clickWidget(240, 14); // Slayer
        sleep(500, 1000);
        Rs2Widget.clickWidget(240, 26);
        sleep(500, 1000);
        Rs2Inventory.interact("Antique lamp", "Rub");
        sleepUntil(() -> Rs2Widget.isWidgetVisible(240, 0), 5000);
        Rs2Widget.clickWidget(240, 14); // Slayer
        sleep(500, 1000);
        Rs2Widget.clickWidget(240, 26);
    }
}
