package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

public class LostCityTask extends AccountBuilderQuestTask {
    public LostCityTask(){
        super(QuestHelperQuest.LOST_CITY,
                new ItemRequirement("Chaos rune", ItemID.CHAOS_RUNE, 150),
                new ItemRequirement("Earth rune", ItemID.EARTH_RUNE, 300),
                new ItemRequirement("Air rune", ItemID.AIR_RUNE, 300),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 10),
                new ItemRequirement("Lumbridge teleport", ItemID.LUMBRIDGE_TELEPORT, 2));

        useFood = true;
    }

    NPC treeSpirit = null;
    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.MONK_OF_ENTRANA_1167){
            if (Rs2Inventory.contains(ItemCollections.AXES.getItems().stream().mapToInt(x -> x).toArray())){
                if (isQuestRunning())
                    stopQuest();

                if (!Rs2Bank.walkToBankAndUseBank())
                    return;

                Rs2Bank.depositEquipment();
                Rs2Bank.depositAllExcept(ItemID.CHAOS_RUNE, ItemID.EARTH_RUNE, ItemID.AIR_RUNE, ItemID.SWORDFISH, ItemID.LUMBRIDGE_TELEPORT, ItemID.KNIFE);
            } else if (!isQuestRunning())
                startupQuest();
        } else if (step.npcID == NpcID.TREE_SPIRIT && (!step.getNpcs().isEmpty() || treeSpirit != null)){
            if (treeSpirit == null)
                treeSpirit = step.getNpcs().get(0);

            if (treeSpirit.isDead()){
                startupQuest();
                return;
            }

            if (isQuestRunning())
                stopQuest();

            var safeSpot = new WorldPoint(2859, 9731, 0);
            if (!Rs2Player.getWorldLocation().equals(safeSpot)){
                if (Rs2Player.getWorldLocation().distanceTo(safeSpot) > 5)
                    Rs2Walker.walkTo(safeSpot);
                else
                    Rs2Walker.walkFastCanvas(safeSpot);

                return;
            }

            Rs2Magic.castOn(MagicAction.CRUMBLE_UNDEAD, treeSpirit);
            Rs2Player.waitForAnimation();
        }
    }

    NPC zombie = null;
    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Kill zombies"))){
            if (Rs2GroundItem.loot(ItemID.BRONZE_PICKAXE))
                return;

            if (zombie == null || zombie.isDead())
                zombie = Rs2Npc.getNpc("Zombie");

            if (zombie != null){
                Rs2Magic.castOn(MagicAction.CRUMBLE_UNDEAD, zombie);
                Rs2Player.waitForAnimation();
            }
        } else if (step.getText().stream().anyMatch(x -> x.contains("Teleport away")))
            Rs2Walker.walkTo(new WorldPoint(3222, 3218, 0));
        else if (step.getText().stream().anyMatch(x -> x.contains("Use a knife"))) {
            Rs2Inventory.combine(ItemID.KNIFE, ItemID.DRAMEN_BRANCH);
            Rs2Inventory.waitForInventoryChanges();
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getRealSkillLevel(Skill.MAGIC) >= 39;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        // TODO leave zanaris

        while (Rs2Inventory.count(ItemID.DRAMEN_BRANCH) > 2) {
            Rs2Inventory.combine(ItemID.KNIFE, ItemID.DRAMEN_BRANCH);
            Rs2Inventory.waitForInventoryChanges();
        }
    }
}
