package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.Arrays;

public class TheDigSiteTask extends AccountBuilderQuestTask {
    public TheDigSiteTask(){
        super(QuestHelperQuest.THE_DIG_SITE);
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("to sync your current quest state."))){
            var quests = Arrays.asList(Rs2Widget.getWidget(399, 7).getChildren());
            var questWidget = quests.stream().filter(x -> x.getText().contains(getQuest().getName())).findFirst().orElse(null);
            var index = quests.indexOf(questWidget);

            Rs2Widget.clickWidgetFast(questWidget, index, 2);
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.DIGSITE_WORKMAN && MQuestScript.getFullText(step).contains("Pickpocket")){
            Rs2Npc.interact(step.getNpcs().get(0), "Steal-from");
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.SOIL_2377){
            Rs2Inventory.dropAll(ItemID.NEEDLE, ItemID.BELT_BUCKLE, ItemID.CERAMIC_REMAINS, ItemID.OLD_TOOTH, ItemID.CLAY, ItemID.PIE_DISH, ItemID.BRONZE_SPEAR, ItemID.BLACK_MED_HELM, ItemID.LEATHER_BOOTS, ItemID.IRON_KNIFE);
        }
    }
}
