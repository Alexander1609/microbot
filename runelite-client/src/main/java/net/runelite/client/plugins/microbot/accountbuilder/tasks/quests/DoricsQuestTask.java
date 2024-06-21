package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;

public class DoricsQuestTask extends AccountBuilderQuestTask {
    public DoricsQuestTask(){
        super(QuestHelperQuest.DORICS_QUEST);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getRealSkillLevel(Skill.MINING) >= 15;
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() + Rs2Inventory.count("ore") + Rs2Inventory.count("clay") < 16){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.BRONZE_PICKAXE)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawItem(ItemID.BRONZE_PICKAXE);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.CLAY, 6)
                || !Rs2Inventory.hasItemAmount(ItemID.COPPER_ORE, 4)
                || !Rs2Inventory.hasItemAmount(ItemID.IRON_ORE, 2)){
            if (!Rs2Inventory.hasItemAmount(ItemID.COPPER_ORE, 4)){
                if (!Rs2Walker.walkTo(new WorldPoint(3029, 9828, 0), 10))
                    return false;

                Rs2GameObject.interact("Copper rocks", "Mine");
                Rs2Player.waitForAnimation();
                return false;
            }

            if (!Rs2Inventory.hasItemAmount(ItemID.IRON_ORE, 2)){
                if (!Rs2Walker.walkTo(new WorldPoint(3029, 9828, 0), 10))
                    return false;

                Rs2GameObject.interact("Iron rocks", "Mine");
                Rs2Player.waitForAnimation();
                return false;
            }

            if (!Rs2Inventory.hasItemAmount(ItemID.CLAY, 6)){
                if (!Rs2Walker.walkTo(new WorldPoint(3026, 9810, 0), 10))
                    return false;

                Rs2GameObject.interact("Clay rocks", "Mine");
                Rs2Player.waitForAnimation();
                return false;
            }

            return false;
        }

        return true;
    }
}
