package net.runelite.client.plugins.microbot.accountbuilder;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.AccountBuilderFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.*;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.steps.PuzzleStep;
import net.runelite.client.plugins.questhelper.steps.WidgetStep;

import java.time.Duration;
import java.util.ArrayList;
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
    @Setter
    AccountBuilderTask nextTask;

    @Getter
    long taskStartTime;

    @Getter
    long taskEndTime;

    @Getter
    Map<AccountBuilderTask, Integer> taskMap;
    boolean taskRunning = false;

    long timeSinceLastAction = 0;
    WorldPoint lastLocation = null;

    long nextCameraRotationTime = 0;

    long nextBreakTime = Long.MAX_VALUE;
    long breakEndTime = 0;

    public boolean run(AccountBuilderConfig config) {
        Microbot.enableAutoRunOn = true;
        task = null;
        taskRunning = false;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!lock.tryLock())
                return;

            try {
                if (handlingBreak(config))
                    return;

                if (!Microbot.isLoggedIn()){
                    if (Microbot.getWorldService() == null)
                        return;

                    new Login(Login.getRandomWorld(config.isMember()));
                    return;
                }

                if (!Microbot.isLoggedIn() || Microbot.getVarbitPlayerValue(281) != 1000)
                    return;

                if (task == null)
                    sleep(1000, 5000);

                handleStuck();

                if (nextCameraRotationTime < System.currentTimeMillis()){
                    nextCameraRotationTime = System.currentTimeMillis() + 60_000 + new Random().nextInt(240_000);

                    Rs2Camera.setPitch(300 + new Random().nextInt(84));
                    Rs2Camera.setAngle(new Random().nextInt(360));
                }

                if (taskMap == null)
                    return;

                if (task == null && nextTask == null && !config.debugMode() && config.autostart())
                    nextTask = getNewRandomTask();

                if (nextTask != null && (task == null || (taskEndTime != 0 && System.currentTimeMillis() > taskEndTime))) {
                    if (task != null)
                        finishTask();

                    task = nextTask;
                    taskStartTime = taskEndTime = 0;

                    task.init();

                    if (task.getFollowUpTask() != null)
                        nextTask = task.getFollowUpTask();
                    else if (config.debugMode())
                        nextTask = null;
                    else
                        nextTask = getNewRandomTask();
                }

                if (task != null && task.isCompleted())
                    finishTask();

                if (task != null && !taskRunning && (config.debugMode() || task.requirementsMet())) {
                    if (!Microbot.isLoggedIn()) return;
                    if (!super.run()) return;
                    if (Rs2Player.isWalking() || Rs2Player.isAnimating()) return;

                    if (!config.debugMode() && !task.doTaskPreparations())
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
                if (ex instanceof InterruptedException) return;

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
                Microbot.log("[AB Exception] " + ex.getMessage());
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

    private void handleStuck(){
        if (Rs2Player.isInteracting()
                || Rs2Player.isAnimating()
                || Rs2Player.isMoving()
                || Rs2Dialogue.isInDialogue()
                || Rs2GrandExchange.isOpen()
                || Rs2Bank.isOpen()
                || !Rs2Player.getWorldLocation().equals(lastLocation)
                || (task != null && task instanceof AccountBuilderQuestTask && ((AccountBuilderQuestTask)task).getCurrentStep() instanceof WidgetStep)
                || (task != null && task instanceof AccountBuilderQuestTask && ((AccountBuilderQuestTask)task).getCurrentStep() instanceof PuzzleStep)
                || (task != null && task.blockStuckPrevention)){
            lastLocation = Rs2Player.getWorldLocation();
            timeSinceLastAction = System.currentTimeMillis();
        } else if (timeSinceLastAction + 10_000 < System.currentTimeMillis()){
            var worldPoints = new ArrayList<>(Rs2Tile.getReachableTilesFromTile(Rs2Player.getWorldLocation(), 5).keySet());
            if (worldPoints.isEmpty())
                return;

            var randomIndex = new Random().nextInt(worldPoints.size());
            Rs2Walker.walkFastCanvas(worldPoints.get(randomIndex));
        }
    }

    private boolean handlingBreak(AccountBuilderConfig config){
        if (nextBreakTime == Long.MAX_VALUE)
            nextBreakTime = System.currentTimeMillis() + ((config.timeUntilBreakStart() + new Random().nextInt(config.timeUntilBreakEnd() - config.timeUntilBreakStart())) * 60_000L);

        if (nextBreakTime < System.currentTimeMillis() && breakEndTime < nextBreakTime){
            if (Microbot.pauseAllScripts
                    || task != null && (task instanceof AccountBuilderFightingTask
                                        || task instanceof AccountBuilderQuestTask // TODO break and restart quest afterwards
                                        || task instanceof AccountBuilderQuestTask && !((AccountBuilderQuestTask)task).isQuestRunning()))
                return false;

            Rs2Player.logout();
            sleepUntil(() -> !Microbot.isLoggedIn());
            if (Microbot.isLoggedIn())
                return false;

            Microbot.pauseAllScripts = true;

            var breakDuration = (config.breakDurationStart() + new Random().nextInt(config.breakDurationEnd() - config.breakDurationStart())) * 60_000L;
            breakEndTime = System.currentTimeMillis() + breakDuration;
            taskEndTime += breakDuration;
        } else if (breakEndTime < System.currentTimeMillis() && nextBreakTime < breakEndTime){
            new Login(Login.getRandomWorld(config.isMember()));
            sleepUntil(Microbot::isLoggedIn);

            if (!Microbot.isLoggedIn())
                return true;

            Microbot.pauseAllScripts = false;
            nextBreakTime = Long.MAX_VALUE;
        }

        return breakEndTime > System.currentTimeMillis();
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

    public void onGameStateChanged(GameStateChanged event)
    {
        if (task != null)
            task.onGameStateChanged(event);
    }

    public void onWallObjectSpawned(WallObjectSpawned event){
        if (task != null)
            task.onWallObjectSpawned(event);
    }

    @Override
    public void shutdown() {
        super.shutdown();

        if (task != null){
            task.doTaskCleanup(true);
            task = null;
        }

        sleepUntil(() -> mainScheduledFuture.isDone());
        Rs2Walker.setTarget(null);
    }
}
