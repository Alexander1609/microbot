package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.GameObject;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.shortestpath.pathfinder.PathfinderConfig;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.puzzlesolver.VarrockMuseumAnswer;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NatualHistoryQuizTask extends AccountBuilderQuestTask {
    boolean started = false;

    GameObject currentPlaque;
    ArrayList<Integer> handledPlaques = new ArrayList<>();
    List<Integer> plaques = List.of(
        ObjectID.PLAQUE_24605,
        ObjectID.PLAQUE_24606,
        ObjectID.PLAQUE_24607,
        ObjectID.PLAQUE_24608,
        ObjectID.PLAQUE_24609,
        ObjectID.PLAQUE_24610,
        ObjectID.PLAQUE_24611,
        ObjectID.PLAQUE_24612,
        ObjectID.PLAQUE_24613,
        ObjectID.PLAQUE_24614,
        ObjectID.PLAQUE_24615,
        ObjectID.PLAQUE_24616,
        ObjectID.PLAQUE_24617,
        ObjectID.PLAQUE_24618
    );

    int currentRoom = 0;
    WorldPoint[] rooms = {
            new WorldPoint(1758, 4939, 0),
            new WorldPoint(1778, 4960, 0),
            new WorldPoint(1772, 4976, 0),
            new WorldPoint(1746, 4976, 0),
            new WorldPoint(1740, 4960, 0)
    };

    public NatualHistoryQuizTask(){
        super(null);
    }

    @Override
    public String getName() {
        return "Quest: Natural history quiz";
    }

    @Override
    public void tick() {
        if (!started || handledPlaques.size() == 14 && !Rs2Dialogue.isInDialogue()){
            if (!Rs2Walker.walkTo(new WorldPoint(1759, 4957, 0)))
                return;

            if (Rs2Dialogue.isInDialogue())
                started = true;
            else
                Rs2Npc.interact(NpcID.ORLANDO_SMITH);

            return;
        }

        if (Rs2Dialogue.isInDialogue()){
            if (Rs2Dialogue.hasSelectAnOption() && Rs2Widget.hasWidget("Sure thing"))
                Rs2Widget.clickWidget("Sure thing");
            else
                Rs2Dialogue.clickContinue();

            return;
        }

        if (currentRoom < rooms.length){
            if (!Rs2Walker.walkTo(rooms[currentRoom], 10))
                return;

            var questionWidget = Rs2Widget.getWidget(ComponentID.VARROCK_MUSEUM_QUESTION);

            if (questionWidget != null){
                var answerWidget = Rs2Widget.findWidget(VarrockMuseumAnswer.MATCHES.get(questionWidget.getText()));
                if (answerWidget == null)
                    return;

                sleep(500, 1000);
                Rs2Widget.clickWidget(answerWidget.getId());
                sleepUntil(() -> Rs2Dialogue.isInDialogue(), 5000);

                if (Rs2Widget.hasWidget("Nice job, mate. That looks about right.")){
                    Rs2Dialogue.clickContinue();
                    sleepUntil(() -> !Rs2Dialogue.isInDialogue() || Rs2Widget.hasWidget("Bonza, mate! I think that's all of them."), 5000);
                }

                if (Rs2Widget.hasWidget("Bonza, mate! I think that's all of them.")){
                    handledPlaques.add(currentPlaque.getId());
                }

                return;
            }

            currentPlaque = Rs2GameObject.getGameObjectsWithinDistance(10).stream()
                    .filter(x -> plaques.contains(x.getId()) && !handledPlaques.contains(x.getId()))
                    .findFirst().orElse(null);

            if (currentPlaque == null){
                currentRoom++;
                return;
            }

            Rs2GameObject.interact(currentPlaque, "Study");
            sleepUntil(() -> Rs2Widget.isWidgetVisible(WidgetInfo.VARROCK_MUSEUM_FIRST_ANSWER), 5000);
        }
    }

    @Override
    public void run(){
        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            try {
                sleep(minTickTime, maxTickTime);

                if (Microbot.pauseAllScripts || !Microbot.isLoggedIn())
                    return;

                tick();
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace(System.out);
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean requirementsMet() {
        return !isCompleted() && Rs2Player.isMember();
    }

    @Override
    public boolean isCompleted() {
        return Microbot.getVarbitValue(3637) >= 28
                && Microbot.getClient().getRealSkillLevel(Skill.HUNTER) >= 9
                && Microbot.getClient().getRealSkillLevel(Skill.SLAYER) >= 9;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        if (scheduledFuture != null && !scheduledFuture.isDone())
            scheduledFuture.cancel(true);

        while (!Rs2Walker.walkTo(new WorldPoint(3258, 3452, 0)))
            sleep(1000, 1500);
    }

    @Override
    public void onGameStateChanged(GameStateChanged event) { }

    @Override
    public boolean doTaskPreparations() {
        return true;
    }
}
