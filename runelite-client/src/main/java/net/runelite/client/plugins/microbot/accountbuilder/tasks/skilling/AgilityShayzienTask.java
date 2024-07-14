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

public class AgilityShayzienTask extends AccountBuilderTask {
    AgilityScript agilityScript = new AgilityScript();

    @Override
    public String getName() {
        return "Agility: Shayzien";
    }

    public AgilityShayzienTask() {
        skill = Skill.AGILITY;
        minLevel = 45;
        maxLevel = 52;

        addRequirement(ItemID.SWORDFISH, 20);
        addRequirement(ItemID.CROSSBOW, true);
        addRequirement(ItemID.MITH_GRAPPLE_9419, true);

        agilityScript.shayzienCourse.add(new AgilityObstacleModel(LADDER_42209));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(MONKEYBARS_42211));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(TIGHTROPE_42212, 1536, -1, Operation.GREATER_EQUAL, Operation.GREATER_EQUAL));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(BEAM, 1520, -1, Operation.GREATER_EQUAL, Operation.GREATER_EQUAL));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(EDGE_42218));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(EDGE_42219));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(BEAM_42220));
        agilityScript.shayzienCourse.add(new AgilityObstacleModel(ZIPLINE));
    }

    @Override
    public void run() {
        super.run();

        agilityScript.run(new MicroAgilityConfig() {
            @Override
            public AgilityCourseLocation agilityCourse() {
                return AgilityCourseLocation.SHAYZIEN_COURSE;
            }
        });
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

        return Rs2Walker.walkTo(new WorldPoint(1552, 3632, 0));
    }
}
