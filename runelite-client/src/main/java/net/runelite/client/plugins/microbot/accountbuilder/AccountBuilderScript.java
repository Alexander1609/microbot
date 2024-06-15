package net.runelite.client.plugins.microbot.accountbuilder;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTaskList;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.MisthalinMysteryTask;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class AccountBuilderScript extends Script {
    public static double version = 1.0;

    @Getter
    AccountBuilderTask task;

    @Getter
    AccountBuilderTask nextTask;

    @Getter
    long taskStartTime;

    @Getter
    long taskEndTime;

    Map<AccountBuilderTask, Integer> taskMap;
    boolean taskRunning = false;

    long timeSinceLastAction = 0;
    WorldPoint lastLocation = null;

    public boolean run(AccountBuilderConfig config) {
        Microbot.enableAutoRunOn = false;
        taskMap = AccountBuilderTaskList.getTasks();
        task = null;
        taskRunning = false;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (task == null && !Microbot.isLoggedIn() || Microbot.getVarbitPlayerValue(281) != 1000)
                    return;

                if (task == null)
                    sleep(1000, 5000);

                // Move randomly if stuck at some point
                if (Rs2Player.isInteracting() || Rs2Player.isAnimating() || Rs2Player.isMoving() || Rs2Dialogue.isInDialogue() || Rs2GrandExchange.isOpen()
                        || !Rs2Player.getWorldLocation().equals(lastLocation)){
                    lastLocation = Rs2Player.getWorldLocation();
                    timeSinceLastAction = System.currentTimeMillis();
                } else if (timeSinceLastAction + 10_000 < System.currentTimeMillis()){
                    List<WorldPoint> worldPoints = Rs2Tile.getWalkableTilesAroundPlayer(5);
                    var randomIndex = new Random().nextInt(worldPoints.size());
                    Rs2Walker.walkFastCanvas(worldPoints.get(randomIndex));
                }

                if (task == null && nextTask == null)
                    nextTask = getNewRandomTask();

                if (nextTask != null && (task == null || (taskEndTime != 0 && System.currentTimeMillis() > taskEndTime))) {
                    if (task != null)
                        finishTask();

                    task = nextTask;
                    taskStartTime = taskEndTime = 0;

                    task.init();
                    nextTask = getNewRandomTask();
                }

                if (task.isCompleted())
                    finishTask();

                if (task != null && task.requirementsMet() && !taskRunning) {
                    if (!Microbot.isLoggedIn()) return;
                    if (!super.run()) return;

                    if (!task.doTaskPreparations())
                        return;

                    if (task.getQuest() == null){
                        var minDuration = (int) Duration.ofMinutes(config.MinTaskDuration()).toMillis();
                        var maxDuration = (int) Duration.ofMinutes(config.MaxTaskDuration()).toMillis();
                        var taskDuration = minDuration + new Random().nextInt(maxDuration - minDuration);
                        taskStartTime = System.currentTimeMillis();
                        taskEndTime = taskStartTime + taskDuration;
                    }

                    taskRunning = true;
                    task.run();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }

    private void finishTask(){
        task.doTaskCleanup(false);
        taskRunning = false;
        task = null;
    }

    private AccountBuilderTask getNewRandomTask(){
        var possibleTasks = taskMap.entrySet().stream().filter(x -> x.getKey().requirementsMet() && !x.getKey().equals(task)).collect(Collectors.toList());

        var totalWeight = (int) possibleTasks.stream().collect(Collectors.summarizingInt(Map.Entry::getValue)).getSum();

        if (totalWeight == 0)
            return null;

        var selectedWeight = new Random().nextInt(totalWeight);

        int currentSum = 0;
        for (var taskEntry : possibleTasks){
            currentSum += taskEntry.getValue();

            if (currentSum > selectedWeight)
                return taskEntry.getKey();
        }

        return null;
    }

    public void onChatMessage(ChatMessage chatMessage) {
        if (task != null)
            task.onChatMessage(chatMessage);
    }

    public void onGameObjectSpawned(GameObjectSpawned event){
        if (task != null)
            task.onGameObjectSpawned(event);
    }

    @Override
    public void shutdown() {
        super.shutdown();

        if (task != null)
            task.doTaskCleanup(true);
    }
}
