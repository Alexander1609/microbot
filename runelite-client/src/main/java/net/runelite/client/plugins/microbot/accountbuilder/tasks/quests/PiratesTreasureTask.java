package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.Arrays;
import java.util.Comparator;

public class PiratesTreasureTask extends AccountBuilderQuestTask {
    public PiratesTreasureTask(){
        super(QuestHelperQuest.PIRATES_TREASURE);
        memberOnly = false;
    }

    @Override
    public void run() {
        super.run();

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(new Restriction(3008, 3207, 0));
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.ZEMBO){
            if (Rs2Widget.hasWidget("Karamjan rum")){
                var widget = Rs2Widget.findWidget("Karamjan rum");
                Rs2Widget.clickWidgetFast(widget, 2, 2);
            }
        }
    }

    boolean gotBananas = false;

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.CRATE_2072 && !gotBananas){
            if (isQuestRunning() && !Rs2Inventory.hasItemAmount(ItemID.BANANA, 10))
                stopQuest();

            if (!Rs2Inventory.hasItemAmount(ItemID.BANANA, 10)){
                if (!Rs2Walker.walkTo(new WorldPoint(2916, 3163, 0)))
                    return;

                var closestBananaTree = Rs2GameObject.getGameObjects().stream().filter(x -> x.getId() >= 2073 && x.getId() < 2078)
                        .sorted(Comparator.comparing(x -> Rs2Player.getWorldLocation().distanceTo(x.getWorldLocation())))
                        .findFirst().orElse(null);

                if (closestBananaTree != null){
                    Rs2GameObject.interact(closestBananaTree, "Pick");
                    sleep(200, 400);
                }
            } else if (!isQuestRunning()){
                startupQuest();
                gotBananas = true;
            }
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Please open Pirate Treasure's Quest Journal to sync the current quest state."))){
            var quests = Arrays.asList(Rs2Widget.getWidget(399, 7).getChildren());
            var questWidget = quests.stream().filter(x -> x.getText().contains(getQuest().getName())).findFirst().orElse(null);
            var index = quests.indexOf(questWidget);

            Rs2Widget.clickWidgetFast(questWidget, index, 2);
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        Rs2Inventory.interact("Casket", "Open");
    }
}
