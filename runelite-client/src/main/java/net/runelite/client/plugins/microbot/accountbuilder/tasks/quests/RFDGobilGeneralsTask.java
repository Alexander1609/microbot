package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class RFDGobilGeneralsTask extends AccountBuilderQuestTask {
    public RFDGobilGeneralsTask(){
        super(QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("I've got the ingredients we need..."))
            Rs2Widget.clickWidget("I've got the ingredients we need...");
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.LADDER_12389){
            while (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1855, 5312, 17, 47, 0)) == 0){
                Rs2GameObject.interact("Barrier", "Pass-trough");
                Rs2Player.waitForWalking();
            }
        } else if (step.objectID == ObjectID.LARGE_DOOR_12349){
            if (Rs2GameObject.interact(12391, "Climb-up"))
                Rs2Player.waitForWalking();
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Use a knife"))){
            if (Rs2Widget.hasWidget("Slice it"))
                Rs2Widget.clickWidget("Slice it");
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        while (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1855, 5312, 17, 47, 0)) == 0){
            Rs2GameObject.interact("Barrier", "Pass-trough");
            Rs2Player.waitForWalking();
        }
    }
}
