package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.helpers.quests.elementalworkshopii.ConnectPipes;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class ElementalWorkshopIITask extends AccountBuilderQuestTask {
    public ElementalWorkshopIITask(){
        super(QuestHelperQuest.ELEMENTAL_WORKSHOP_II,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemCollections.COMBAT_BRACELETS, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (currentStep instanceof ConnectPipes){
            blockStuckPrevention = true;

            if (Rs2Widget.isWidgetVisible(262, 41)) {
                Rs2Widget.clickWidget(262, 42);
                sleep(200, 600);
                Rs2Widget.clickWidget(262, 37);
                sleep(200, 600);

                Rs2Widget.clickWidget(262, 41);
                sleep(200, 600);
                Rs2Widget.clickWidget(262, 40);
                sleep(200, 600);

                Rs2Widget.clickWidget(262, 38);
                sleep(200, 600);
                Rs2Widget.clickWidget(262, 39);
                sleepUntil(() -> !(currentStep instanceof ConnectPipes), 10000);
            }
        } else {
            blockStuckPrevention = false;
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Pick up the elemental ore."))){
            Rs2GroundItem.loot(ItemID.ELEMENTAL_ORE);
            Rs2Player.waitForWalking();
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.WORKBENCH_3402){
            if (Rs2Widget.hasWidget("Crane claw")) {
                Rs2Widget.clickWidget("Crane claw");
                Rs2Inventory.waitForInventoryChanges();
                Rs2Walker.walkFastCanvas(Rs2Player.getWorldLocation());
            }
        } else if (step.objectID == ObjectID.CRATE_18612){
            if (Rs2Inventory.contains("Small cog") && Rs2Player.getWorldLocation().getPlane() == 3 && Rs2Walker.walkTo(1948, 5149, 3))
                Rs2GameObject.interact("Stairs", "Climb-down");
        } else if (step.objectID == NullObjectID.NULL_3414){
            Rs2Walker.walkTo(1953, 5167, 3);
        } else if (step.objectID == ObjectID.STAIRS_18611 && Rs2Walker.walkTo(1948, 5149, 3)){
            Rs2GameObject.interact("Stairs", "Climb-down");
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet();
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
