package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public abstract class AccountBuilderTask {
    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    private boolean canceled;

    protected boolean memberOnly = true;
    public boolean blockStuckPrevention = false;
    protected Skill skill = null;
    protected int minLevel = 0;
    protected int maxLevel = Integer.MAX_VALUE;

    protected int minTickTime = 500;
    protected int maxTickTime = 1500;

    public abstract String getName();

    public boolean requirementsMet(){
        if (skill != null){
            var level = Microbot.getClient().getRealSkillLevel(skill);

            if (level < minLevel || level > maxLevel)
                return false;
        }

        if (memberOnly && !Rs2Player.isMember())
            return false;

        return true;
    }

    public boolean isCompleted(){
        return canceled;
    }

    public void doTaskCleanup(boolean shutdown) {
        if (scheduledFuture != null && !scheduledFuture.isDone())
            scheduledFuture.cancel(true);
    }

    public void init(){
        canceled = false;
    }

    public void tick(){ }

    public void run(){
        scheduledFuture = executorService.scheduleWithFixedDelay(() -> {
            try {
                if (Microbot.enableAutoRunOn)
                    Rs2Player.toggleRunEnergy(true);

                if (Rs2Widget.getWidget(15269889) != null) {
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                }

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

    public boolean doTaskPreparations(){ return true; }

    public void cancel(){
        canceled = true;
        System.out.printf("[AIO Account builder] Task '%s' canceled%n", getName());
    }

    public void onChatMessage(ChatMessage chatMessage) { }
    public void onGameObjectSpawned(GameObjectSpawned event){ }
    public void onGameTick(GameTick gameTick) { }
    public void onGameStateChanged(GameStateChanged event) { }

    protected void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void sleep(int time, int maxTime) {
        try {
            Thread.sleep(Random.random(time, maxTime));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    protected boolean sleepUntil(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < time);
        return done;
    }

    protected boolean isQuestCompleted(QuestHelperQuest questHelperQuest){
        return Microbot.getClientThread().runOnClientThread(() -> questHelperQuest.getQuestHelper().isCompleted());
    }
}
