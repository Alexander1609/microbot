package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class TribalTotemTask extends AccountBuilderQuestTask {
    public TribalTotemTask(){
        super(QuestHelperQuest.TRIBAL_TOTEM);
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.DOOR_2705){
            if (Rs2Widget.isWidgetVisible(369, 0)){
                if (isQuestRunning()) {
                    stopQuest();
                    blockStuckPrevention = true;
                }

                if (!Rs2Widget.getWidget(369, 12).getText().equals("K"))
                    Rs2Widget.clickWidget(369, 72);
                else if (!Rs2Widget.getWidget(369, 13).getText().equals("U"))
                    Rs2Widget.clickWidget(369, 73);
                else if (!Rs2Widget.getWidget(369, 14).getText().equals("R"))
                    Rs2Widget.clickWidget(369, 76);
                else if (!Rs2Widget.getWidget(369, 15).getText().equals("T"))
                    Rs2Widget.clickWidget(369, 77);
                else
                    Rs2Widget.clickWidget(369, 59);
            } else if (!isQuestRunning()) {
                startupQuest();
                blockStuckPrevention = false;
            }
        } else if (step.objectID == ObjectID.STAIRS_2711){
            if (Rs2Widget.hasWidget("Your trained senses"))
                Rs2GameObject.interact(ObjectID.STAIRS_2711, "Climb-up");
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
