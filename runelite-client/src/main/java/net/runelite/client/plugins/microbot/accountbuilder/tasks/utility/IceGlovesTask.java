package net.runelite.client.plugins.microbot.accountbuilder.tasks.utility;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;

public class IceGlovesTask extends AccountBuilderTask {
    FoodScript foodScript = new FoodScript();

    @Override
    public String getName() {
        return "Utility: Getting ice gloves";
    }

    public IceGlovesTask(){
        addRequirement(ItemID.WIZARD_HAT, true);
        addRequirement(ItemID.ZAMORAK_MONK_TOP, true);
        addRequirement(ItemID.ZAMORAK_MONK_BOTTOM, true);
        addRequirement(ItemID.AMULET_OF_MAGIC, true);
        addRequirement(ItemID.LEATHER_BOOTS, true);
        addRequirement(ItemID.STAFF_OF_FIRE, true);
        addRequirement(ItemCollections.ARDY_CLOAKS, true);
        addRequirement(ItemID.ADAMANT_PICKAXE, 1);
        addRequirement(ItemID.DEATH_RUNE, 200);
        addRequirement(ItemID.AIR_RUNE, 800);
        addRequirement(ItemID.SWORDFISH, 20);
        addRequirement(ItemID.SUPER_ENERGY4, 2);
        addRequirement(ItemID.STAMINA_POTION4, 2);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.MAGIC) > 65
                && Microbot.getClient().getRealSkillLevel(Skill.MINING) >= 50
                && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 50
                && Rs2Bank.bankItems != null
                && !Rs2Bank.hasBankItem(ItemID.ICE_GLOVES, 1)
                && !Rs2Inventory.contains(ItemID.ICE_GLOVES)
                && !Rs2Equipment.isWearing(ItemID.ICE_GLOVES);
    }

    @Override
    public void run() {
        super.run();

        foodScript.run(new PlayerAssistConfig() {
            @Override
            public boolean toggleFood() {
                return true;
            }
        });
    }

    @Override
    public void tick() {
        var safeSpot = new WorldPoint(2875, 9955, 0);

        if (Rs2GroundItem.loot(ItemID.ICE_GLOVES)){
            Rs2Inventory.waitForInventoryChanges();
            return;
        }

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2852, 9951, 28, 15, 0)) != 0){
            Rs2Walker.walkTo(safeSpot);
        } else if (!Rs2Player.getWorldLocation().equals(safeSpot)){
            Rs2Walker.walkFastCanvas(safeSpot);
            Rs2Player.waitForWalking();
        } else if (Rs2Npc.attack(NpcID.ICE_QUEEN)){
            Rs2Player.waitForAnimation();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        if (Rs2Bank.isOpen()){
            Rs2Bank.closeBank();
            return false;
        }

        if (Rs2Tab.getCurrentTab() != InterfaceTab.COMBAT) {
            Rs2Tab.switchToCombatOptionsTab();
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.COMBAT, 2000);
        }

        sleep(100, 200);
        Rs2Widget.clickWidget(WidgetInfo.COMBAT_SPELL_BOX);
        sleepUntil(() -> Rs2Widget.isWidgetVisible(201, 1), 5000);
        var childs = Rs2Widget.getWidget(201, 1).getDynamicChildren();
        Rs2Widget.clickWidgetFast(childs[12], 12);

        return true;
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || Rs2Inventory.contains(ItemID.ICE_GLOVES);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        Rs2Walker.walkTo(new WorldPoint(2603, 3230, 0));
        Rs2Player.waitForWalking();

        super.doTaskCleanup(shutdown);

        foodScript.shutdown();
    }
}
