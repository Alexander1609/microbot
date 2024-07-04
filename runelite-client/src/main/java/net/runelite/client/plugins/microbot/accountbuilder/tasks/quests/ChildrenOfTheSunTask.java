package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.NpcID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class ChildrenOfTheSunTask extends AccountBuilderQuestTask {
    int guardStep = 0;

    public ChildrenOfTheSunTask(){
        super(QuestHelperQuest.CHILDREN_OF_THE_SUN, false);
        minTickTime = 50;
        maxTickTime = 100;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.GUARD_12661){
            if (isQuestRunning()){
                stopQuest();
                blockStuckPrevention = true;
                Microbot.enableAutoRunOn = false;
            }

            var guard = Rs2Npc.getNpc(NpcID.GUARD_12661);
            if (guard == null)
                return;

            if (!Rs2Camera.isTileOnScreen(guard.getLocalLocation()))
                Rs2Camera.turnTo(guard);

            if (guardStep == 0){
                if (!Rs2Player.getWorldLocation().equals(new WorldPoint(3233, 3427, 0))){
                    Rs2Walker.walkFastCanvas(new WorldPoint(3233, 3427, 0), false);
                    Rs2Player.waitForWalking();
                }

                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3239, 3429, 0)))
                    guardStep++;
            } else if (guardStep == 1){
                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3239, 3429, 0)))
                    return;

                if (!Rs2Player.getWorldLocation().equals(new WorldPoint(3240, 3417, 0))){
                    Rs2Walker.walkFastCanvas(new WorldPoint(3240, 3417, 0), false);
                    Rs2Player.waitForWalking();
                }

                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3243, 3412, 0)))
                    guardStep++;
            } else if (guardStep == 2){
                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3243, 3412, 0)))
                    return;

                if (!Rs2Player.getWorldLocation().equals(new WorldPoint(3241, 3403, 0))){
                    Rs2Walker.walkFastCanvas(new WorldPoint(3241, 3403, 0), false);
                    Rs2Player.waitForWalking();
                }

                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3239, 3401, 0)))
                    guardStep++;
            } else if (guardStep == 3){
                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3239, 3401, 0)))
                    return;

                if (!Rs2Player.getWorldLocation().equals(new WorldPoint(3236, 3392, 0))){
                    Rs2Walker.walkFastCanvas(new WorldPoint(3236, 3392, 0), false);
                    Rs2Player.waitForWalking();
                }

                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3243, 3389, 0)))
                    guardStep++;
            } else if (guardStep == 4){
                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).equals(new WorldPoint(3243, 3389, 0)))
                    return;

                if (!Rs2Player.getWorldLocation().equals(new WorldPoint(3247, 3397, 0))){
                    Rs2Walker.walkFastCanvas(new WorldPoint(3247, 3397, 0), false);
                    Rs2Player.waitForWalking();
                }

                if (WorldPoint.fromLocalInstance(Microbot.getClient(), guard.getLocalLocation()).distanceTo(new WorldArea(3259, 3396, 2, 8, 0)) == 0) {
                    if (!isQuestRunning()) {
                        startupQuest();
                        blockStuckPrevention = false;
                        Microbot.enableAutoRunOn = true;
                    }
                }
            }
        }
    }
}
