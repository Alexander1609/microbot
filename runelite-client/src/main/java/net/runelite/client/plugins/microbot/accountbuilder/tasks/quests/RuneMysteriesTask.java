package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;

public class RuneMysteriesTask extends AccountBuilderQuestTask {
    public RuneMysteriesTask(){
        super(QuestHelperQuest.RUNE_MYSTERIES);
    }

    @Override
    public void tick() {
        super.tick();

        //Lumbridge stair bug
        if (Rs2Player.getWorldLocation().equals(new WorldPoint(3203, 3208, 0)))
            Rs2GameObject.interact(16671);

        if (Rs2Player.getWorldLocation().equals(new WorldPoint(3105, 3166, 0)))
            Rs2Walker.walkTo(new WorldPoint(3109, 3167, 0), 0);
    }
}
