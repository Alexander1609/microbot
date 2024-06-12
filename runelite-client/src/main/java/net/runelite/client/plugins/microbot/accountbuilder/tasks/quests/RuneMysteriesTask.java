package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.concurrent.TimeUnit;

public class RuneMysteriesTask extends AccountBuilderTask {
    public RuneMysteriesTask(){
        quest = QuestHelperQuest.RUNE_MYSTERIES;
    }

    @Override
    public void run() {
        super.run();

        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            //Lumbridge stair bug
            if (Rs2Player.getWorldLocation().equals(new WorldPoint(3203, 3208, 0)))
                Rs2GameObject.interact(16671);

            if (Rs2Player.getWorldLocation().equals(new WorldPoint(3105, 3166, 0)))
                Rs2Walker.walkTo(new WorldPoint(3109, 3167, 0), 0);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }
}
