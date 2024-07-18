package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class DemonSlayerTask extends AccountBuilderQuestTask {
    public DemonSlayerTask(){
        super(QuestHelperQuest.DEMON_SLAYER);

        ignoreRecommendedRequirements = true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 45;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.MANHOLE_882){
            if (Rs2Inventory.contains(ItemID.BUCKET))
                Rs2Inventory.drop(ItemID.BUCKET);
            else if (Rs2Inventory.contains(ItemCollections.GOOD_EATING_FOOD.getItems().toArray(new Integer[0])))
                Rs2Inventory.dropAll(ItemCollections.GOOD_EATING_FOOD.getItems().toArray(new Integer[0]));
        }
    }
}
