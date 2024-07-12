package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class XMarksTheSpotTask extends AccountBuilderQuestTask {
    public XMarksTheSpotTask() {
        super(QuestHelperQuest.X_MARKS_THE_SPOT);
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
    }
}
