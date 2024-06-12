package net.runelite.client.plugins.microbot.accountbuilder;

import lombok.Getter;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTaskList;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.RuneMysteriesTask;

import java.time.Duration;
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

    public boolean run(AccountBuilderConfig config) {
        Microbot.enableAutoRunOn = false;
        taskMap = AccountBuilderTaskList.getTasks();
        task = null;
        taskRunning = false;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (task == null && !Microbot.isLoggedIn())
                    return;

                if (task == null && nextTask == null)
                    nextTask = getNewRandomTask();

                if (nextTask != null && (task == null || (taskEndTime != 0 && System.currentTimeMillis() > taskEndTime))) {
                    task = nextTask;
                    taskStartTime = taskEndTime = 0;

                    task.init();
                    nextTask = getNewRandomTask();
                }

                if (task.isCompleted()){
                    task.doTaskCleanup(false);
                    taskRunning = false;
                    task = null;
                }

                if (task != null && task.requirementsMet() && !taskRunning) {
                    if (!Microbot.isLoggedIn()) return;
                    if (!super.run()) return;

                    if (!task.doTaskPreparations())
                        return;

                    var minDuration = (int) Duration.ofMinutes(config.MinTaskDuration()).toMillis();
                    var maxDuration = (int) Duration.ofMinutes(config.MaxTaskDuration()).toMillis();
                    var taskDuration = minDuration + new Random().nextInt(maxDuration - minDuration);
                    taskStartTime = System.currentTimeMillis();
                    taskEndTime = taskStartTime + taskDuration;

                    taskRunning = true;
                    task.run();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
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

    @Override
    public void shutdown() {
        super.shutdown();

        if (task != null)
            task.doTaskCleanup(true);
    }
}
