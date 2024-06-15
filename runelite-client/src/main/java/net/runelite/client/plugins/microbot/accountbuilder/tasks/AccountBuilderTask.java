package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.quest.MQuestConfig;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.MQuestHelperPlugin;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BooleanSupplier;

public class AccountBuilderTask {
    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    private boolean canceled;

    protected Skill skill = null;
    protected int minLevel = 0;
    protected int maxLevel = Integer.MAX_VALUE;

    @Getter
    protected QuestHelperQuest quest = null;
    private MQuestScript questScript;

    public boolean requirementsMet(){
        if (skill != null){
            var level = Microbot.getClient().getRealSkillLevel(skill);

            if (level < minLevel || level > maxLevel)
                return false;
        }

        if (quest != null && Microbot.getClientThread().runOnClientThread(() ->quest.getQuestHelper().isCompleted()))
            return false;

        return true;
    }

    public boolean isCompleted(){
        return canceled || quest != null && Microbot.getClientThread().runOnClientThread(() -> quest.getQuestHelper().isCompleted());
    }

    public void doTaskCleanup(boolean shutdown) {
        if (scheduledFuture != null && !scheduledFuture.isDone())
            scheduledFuture.cancel(true);

        if (questScript != null){
            questScript.shutdown();
            questScript = null;
        }

        if (quest != null && !shutdown){
            sleepUntil(() -> Rs2Widget.getWidget(10027025) != null, 30_000);
            Rs2Widget.clickWidget(10027025);
        }
    }

    public void init(){
        canceled = false;
    }

    public void run(){
        if (quest != null){
            questScript = new MQuestScript();
            questScript.run(new MQuestConfig(){ });
            startupQuest();
            /*var questPlugin = getQuestPlugin();
            if (!Microbot.getPluginManager().isPluginEnabled(questPlugin)){
                Microbot.getPluginManager().setPluginEnabled(questPlugin, true);
                Microbot.getClientThread().runOnClientThread(() -> { Microbot.getPluginManager().startPlugin(questPlugin); return null; });
            }*/
        }
    }

    public boolean doTaskPreparations(){ return true; }

    public void cancel(){
        canceled = true;
        System.out.printf("[AIO Account builder] Task '%s' canceled%n", getName());
    }

    public String getName(){
        if (quest != null)
            return String.format("Quest - %s", quest.getName());

        return "{MISSING NAME}";
    }

    public void onChatMessage(ChatMessage chatMessage) { }
    public void onGameObjectSpawned(GameObjectSpawned event){ }

    protected void startupQuest(){
        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().startUpQuest(quest.getQuestHelper()); return null; });
    }

    protected void stopQuest(){
        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().shutDownQuestFromSidebar(); return null; });
    }

    protected boolean isQuestRunning(){
        return Microbot.getClientThread().runOnClientThread(() -> MQuestHelperPlugin.getSelectedQuest() != null);
    }

    protected MQuestHelperPlugin getQuestHelperPlugin() {
        return (MQuestHelperPlugin)Microbot.getPluginManager().getPlugins().stream().filter(x -> x instanceof MQuestHelperPlugin).findFirst().orElse(null);
    }

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
}
