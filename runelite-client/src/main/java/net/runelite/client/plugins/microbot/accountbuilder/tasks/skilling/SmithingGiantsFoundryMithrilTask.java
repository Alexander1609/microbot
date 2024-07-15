package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryConfig;
import net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryScript;
import net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryState;
import net.runelite.client.plugins.microbot.giantsfoundry.enums.SmithableBars;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class SmithingGiantsFoundryMithrilTask extends AccountBuilderTask {
    GiantsFoundryScript giantsFoundryScript = new GiantsFoundryScript();

    @Override
    public String getName() {
        return "Smithing: Giants foundry (Steel/Mithril)";
    }

    public SmithingGiantsFoundryMithrilTask(){
        skill = Skill.SMITHING;
        minLevel = 50;
        maxLevel = 70;

        addRequirement(ItemID.ICE_GLOVES, true);
        addRequirement(ItemID.STEEL_BAR, 300);
        addRequirement(ItemID.MITHRIL_BAR, 300);
    }

    @Override
    public void run() {
        super.run();

        giantsFoundryScript.run(new GiantsFoundryConfig() {
            @Override
            public SmithableBars FirstBar() {
                return SmithableBars.STEEL_BAR;
            }

            @Override
            public SmithableBars SecondBar() {
                return SmithableBars.MITHRIL_BAR;
            }
        });
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && isQuestCompleted(QuestHelperQuest.SLEEPING_GIANTS);
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Equipment.hasEquippedSlot(EquipmentInventorySlot.WEAPON) && Rs2Inventory.getEmptySlots() > 0) {
            Rs2Equipment.remove(EquipmentInventorySlot.WEAPON);
            inventoryCleared = false;
        } else if (Rs2Equipment.hasEquippedSlot(EquipmentInventorySlot.SHIELD) && Rs2Inventory.getEmptySlots() > 0){
            Rs2Equipment.remove(EquipmentInventorySlot.SHIELD);
            inventoryCleared = false;
        }

        if (!clearInventory() || !withdrawBuyItems())
            return false;

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3336, 11476, 61, 44, 0)) != 0
                && !Rs2Walker.walkTo(new WorldPoint(3361, 3148, 0)))
            return false;

        if (Rs2GameObject.interact(44635, "Enter")) {
            Rs2Player.waitForWalking();
            return false;
        }

        return true;
    }

    private int previousHeat = 0;
    private static final int VARBIT_HEAT = 13948;

    @Override
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == VARBIT_HEAT)
        {
            // ignore passive heat decay, one heat per two ticks
            if (event.getValue() - previousHeat != -1)
            {

                GiantsFoundryState.heatingCoolingState.onTick();
            }
            previousHeat = event.getValue();
        }
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        var hasSword = Rs2Equipment.get(EquipmentInventorySlot.WEAPON) != null;
        while (hasSword){
            if (Global.sleepUntilTrue(() -> Rs2Equipment.get(EquipmentInventorySlot.WEAPON) == null, 10, 1000))
                hasSword = false;
        }

        super.doTaskCleanup(shutdown);

        giantsFoundryScript.shutdown();
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || running && !giantsFoundryScript.isRunning();
    }
}
