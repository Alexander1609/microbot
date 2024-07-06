package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.globval.WidgetIndices;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class BuildingCrudeChairTask extends AccountBuilderTask {
    @Override
    public String getName() {
        return "Building: Crude chair";
    }

    public BuildingCrudeChairTask(){
        skill = Skill.CONSTRUCTION;
        minLevel = 8;
        maxLevel = 10;

        itemRequirements.add(new ItemRequirement("Planks", ItemID.PLANK, 8));
        itemRequirements.add(new ItemRequirement("Feather", ItemID.BRONZE_NAILS, 30));
        itemRequirements.add(new ItemRequirement("Hammer", ItemID.HAMMER));
        itemRequirements.add(new ItemRequirement("Saw", ItemID.SAW));
        itemRequirements.add(new ItemRequirement("House teleport", ItemID.TELEPORT_TO_HOUSE));
    }

    @Override
    public void tick() {
        if (Rs2Bank.isOpen()){
            Rs2Bank.closeBank();
            return;
        }

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1774, 5633, 273, 148, 0)) != 0) {
            Rs2Inventory.interact(ItemID.TELEPORT_TO_HOUSE, "Inside");
            return;
        }

        if (Microbot.getVarbitValue(2176) != 1){
            Rs2Tab.switchToSettingsTab();
            sleep(800, 1800);
            Widget houseOptionWidget = Rs2Widget.findWidget(SpriteID.OPTIONS_HOUSE_OPTIONS, null);
            if (houseOptionWidget != null)
                Microbot.getMouse().click(houseOptionWidget.getCanvasLocation());
            sleep(800, 1800);
            Rs2Widget.clickWidget(370, 5);
            return;
        }

        var chair = Rs2GameObject.get("Chair", true);
        if (chair != null) {
            Rs2GameObject.interact(chair, "Remove");
            sleepUntil(() -> Rs2Widget.hasWidget("Really remove it?"), 5000);
            Rs2Widget.clickWidget("Yes");
            Rs2Player.waitForAnimation();
            return;
        }

        if (!Rs2Inventory.contains(ItemID.PLANK)){
            Rs2GameObject.interact("Portal", "Enter");
            Rs2Player.waitForAnimation();

            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(1774, 5633, 273, 148, 0)) != 0)
                cancel();
        }

        if (!Rs2Widget.isWidgetVisible(458, 0)) {
            Rs2GameObject.interact("Chair space", "Build");
            sleep(500, 700);
        }
        else {
            Rs2Widget.clickWidget(458, 4);
            Rs2Player.waitForAnimation();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
