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
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.misc.Operation;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.worldmap.AgilityCourseLocation;

import static net.runelite.api.ObjectID.*;

public class AgilityBarbarianOutpostTask extends AccountBuilderTask {
    AgilityScript agilityScript = new AgilityScript();

    @Override
    public String getName() {
        return "Agility: Barbarian Outpost";
    }

    public AgilityBarbarianOutpostTask(){
        skill = Skill.AGILITY;
        minLevel = 35;
        maxLevel = 45;

        addRequirement(ItemID.SWORDFISH, 20);

        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(ROPESWING_23131, 2543, 3552, Operation.GREATER_EQUAL, Operation.GREATER_EQUAL));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(LOG_BALANCE_23144, 2541, 3551, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(OBSTACLE_NET_20211, 2539, 3551, Operation.GREATER_EQUAL, Operation.LESS_EQUAL));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(BALANCING_LEDGE_23547));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(LADDER_42487));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(CRUMBLING_WALL_1948, new WorldPoint(2536, 3553, 0), 2537, -1, Operation.LESS, Operation.GREATER_EQUAL));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(CRUMBLING_WALL_1948, new WorldPoint(2539, 3553, 0), 2540, -1, Operation.LESS, Operation.GREATER_EQUAL));
        agilityScript.barbarianOutpostCourse.add(new AgilityObstacleModel(CRUMBLING_WALL_1948, new WorldPoint(2542, 3553, 0), 2543, -1, Operation.LESS, Operation.GREATER_EQUAL));
    }

    @Override
    public void run() {
        super.run();

        agilityScript.run(new MicroAgilityConfig() {
            @Override
            public AgilityCourseLocation agilityCourse() {
                return AgilityCourseLocation.BARBARIAN_OUTPOST_AGILITY_COURSE;
            }
        });
    }

    long tickStuck = 0;

    @Override
    public void tick() {
        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2543, 9946, 15, 12, 0)) == 0){
            Rs2GameObject.interact(17385);
            Rs2Player.waitForAnimation();
        } else if (Rs2Player.getWorldLocation().equals(new WorldPoint(2532, 3546, 0))){
            sleep(500, 900);
            var wall = Rs2GameObject.findObjectByLocation(new WorldPoint(2536, 3553, 0));
            Rs2GameObject.interact(wall);
            Rs2Player.waitForAnimation();
        }
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        var onCourse = Rs2Player.getWorldLocation().distanceTo(new WorldArea(2543, 3549, 10, 9, 0)) != 0;
        while (onCourse){
            if (Global.sleepUntilTrue(() -> Rs2Player.getWorldLocation().distanceTo(new WorldArea(2543, 3549, 10, 9, 0)) == 0, 10, 1000))
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

        return Rs2Walker.walkTo(new WorldPoint(2551, 3554, 0));
    }
}
