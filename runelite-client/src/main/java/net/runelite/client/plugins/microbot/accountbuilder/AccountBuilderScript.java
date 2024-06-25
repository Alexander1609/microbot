package net.runelite.client.plugins.microbot.accountbuilder;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTaskList;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.AccountBuilderQuestTask;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.steps.WidgetStep;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class AccountBuilderScript extends Script {
    public static double version = 1.0;
    ReentrantLock lock = new ReentrantLock();

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

    long nextCameraRotationTime = 0;

    public boolean run(AccountBuilderConfig config) {
        taskMap = AccountBuilderTaskList.getTasks();
        task = null;
        taskRunning = false;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!lock.tryLock())
                return;

            try {
                if (task == null && !Microbot.isLoggedIn() || Microbot.getVarbitPlayerValue(281) != 1000)
                    return;

                if (task == null)
                    sleep(1000, 5000);

                // Move randomly if stuck at some point
                if (Rs2Player.isInteracting()
                        || Rs2Player.isAnimating()
                        || Rs2Player.isMoving()
                        || Rs2Dialogue.isInDialogue()
                        || Rs2GrandExchange.isOpen()
                        || !Rs2Player.getWorldLocation().equals(lastLocation)
                        || (task != null && task instanceof AccountBuilderQuestTask && ((AccountBuilderQuestTask)task).getCurrentStep() instanceof WidgetStep)){
                    lastLocation = Rs2Player.getWorldLocation();
                    timeSinceLastAction = System.currentTimeMillis();
                } else if (timeSinceLastAction + 10_000 < System.currentTimeMillis()){
                    var worldPoints = new ArrayList<>(Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), 5).keySet());
                    var randomIndex = new Random().nextInt(worldPoints.size());
                    Rs2Walker.walkFastCanvas(worldPoints.get(randomIndex));
                }

                if (nextCameraRotationTime < System.currentTimeMillis()){
                    nextCameraRotationTime = System.currentTimeMillis() + 60_000 + new Random().nextInt(240_000);

                    Rs2Camera.setPitch(300 + new Random().nextInt(84));
                    Rs2Camera.setAngle(new Random().nextInt(360));
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

                if (task != null && task.isCompleted())
                    finishTask();

                if (task != null && task.requirementsMet() && !taskRunning) {
                    if (!Microbot.isLoggedIn()) return;
                    if (!super.run()) return;
                    if (Rs2Player.isWalking() || Rs2Player.isAnimating()) return;

                    if (!task.doTaskPreparations())
                        return;

                    if (!(task instanceof AccountBuilderQuestTask)){
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
                ex.printStackTrace(System.out);
            } finally {
                lock.unlock();
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

    public void onGameTick(GameTick gameTick) {
        if (task != null)
            task.onGameTick(gameTick);
    }

    @Override
    public void shutdown() {
        super.shutdown();

        sleepUntil(() -> mainScheduledFuture.isDone());

        if (ShortestPathPlugin.getMarker() != null)
            ShortestPathPlugin.exit();

        if (task != null)
            task.doTaskCleanup(true);
    }
}
