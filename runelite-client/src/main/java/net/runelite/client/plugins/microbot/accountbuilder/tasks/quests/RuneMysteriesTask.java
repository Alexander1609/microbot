package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class RuneMysteriesTask extends AccountBuilderQuestTask {
    public RuneMysteriesTask(){
        super(QuestHelperQuest.RUNE_MYSTERIES);
        memberOnly = false;
    }

    @Override
    public void tick() {
        super.tick();

        //Lumbridge stair bug
        if (Rs2Player.getWorldLocation().equals(new WorldPoint(3203, 3208, 0)))
            Rs2GameObject.interact(16671);

        if (Rs2Player.getWorldLocation().equals(new WorldPoint(3105, 3166, 0)))
            Rs2Walker.walkTo(new WorldPoint(3109, 3167, 0), 0);

        if (Rs2Dialogue.isInDialogue() && Rs2Widget.hasWidget("Climb down"))
            Rs2Widget.clickWidget("Climb down");
    }
}
