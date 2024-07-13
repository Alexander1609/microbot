package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.WorldPointUtil;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.Requirement;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class JunglePotionTask extends AccountBuilderQuestTask{
    public JunglePotionTask() {
        super(QuestHelperQuest.JUNGLE_POTION, new ItemRequirement("Ring of wealth(5)", ItemID.RING_OF_WEALTH_5, 1, true),
                new ItemRequirement("Amulet of glory(6)", ItemID.AMULET_OF_GLORY6, 1, true),
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 15) ,new ItemRequirement("Antidote++(4)", ItemID.ANTIDOTE4_5952, 2), new ItemRequirement("Stamina potion(4)", ItemID.STAMINA_POTION4, 2));

        useFood = true;
        useAntiPoison = true;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if(step.objectID == 2583){
            if (isQuestRunning())
                stopQuest();
            WorldPoint safeSpot = new WorldPoint(2830, 9492, 0);
            if (!Rs2Player.getWorldLocation().equals(safeSpot)){
                Rs2Walker.walkFastCanvas(safeSpot);
            } else {
                var wall = Rs2GameObject.getTileObjects(2583, safeSpot.dy(-1)).get(0);
                Rs2GameObject.interact(wall, "Search");
                Rs2Player.waitForAnimation();
                sleepUntil(() -> Rs2Inventory.hasItem("Grimy rogue's purse"), 30000);
            }
        } else if(step.objectID == 2584){
            Rs2GameObject.interact(step.objectID, "Search");
            Rs2Player.waitForAnimation();
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) > 25;
    }
}

