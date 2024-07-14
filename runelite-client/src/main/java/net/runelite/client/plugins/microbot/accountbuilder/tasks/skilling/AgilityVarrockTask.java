package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.agility.AgilityScript;
import net.runelite.client.plugins.microbot.agility.MicroAgilityConfig;
import net.runelite.client.plugins.microbot.agility.models.AgilityObstacleModel;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.misc.Operation;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.worldmap.AgilityCourseLocation;

import static net.runelite.api.ObjectID.*;

public class AgilityVarrockTask extends AccountBuilderTask {
    AgilityScript agilityScript = new AgilityScript();

    @Override
    public String getName() {
        return "Agility: Varrock";
    }

    public AgilityVarrockTask(){
        skill = Skill.AGILITY;
        minLevel = 30;
        maxLevel = 40;

        addRequirement(ItemID.SWORDFISH, 20);

        agilityScript.varrockCourse.add(new AgilityObstacleModel(ROUGH_WALL_14412));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(CLOTHES_LINE));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14414));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(WALL_14832));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14833)); // this obstacle doesn't always work for some reason
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14834));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14835, -1, 3402, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(LEDGE_14836, -1, 3408, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(EDGE));
    }

    @Override
    public void run() {
        super.run();

        agilityScript.run(new MicroAgilityConfig() {
            @Override
            public AgilityCourseLocation agilityCourse() {
                return AgilityCourseLocation.VARROCK_ROOFTOP_COURSE;
            }
        });
    }

    long tickStuck = 0;

    @Override
    public void tick() {
        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3177, 3380, 25, 20, 3)) == 0){
            if (tickStuck == 0)
                tickStuck = Microbot.getClient().getTickCount();
            else if (tickStuck + 20 < Microbot.getClient().getTickCount()){
                Rs2Walker.walkTo(new WorldPoint(3208, 3399, 3));
            }
        } else
            tickStuck = 0;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        var onCourse = Microbot.getClient().getPlane() != 0;
        while (onCourse){
            if (Global.sleepUntilTrue(() -> Microbot.getClient().getPlane() == 0, 10, 1000))
                onCourse = false;
        }

        super.doTaskCleanup(shutdown);

        agilityScript.shutdown();
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || running && !agilityScript.isRunning();
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        return Rs2Walker.walkTo(new WorldPoint(3222, 3414, 0));
    }
}
