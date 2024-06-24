package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.quest.MQuestConfig;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.MQuestHelperPlugin;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.*;

public abstract class AccountBuilderQuestTask extends AccountBuilderTask {
    private final QuestHelperQuest quest;
    private final MQuestScript questScript  = new MQuestScript();
    private final FoodScript foodScript = new FoodScript();

    protected QuestStep currentStep;

    protected boolean useFood = false;

    public AccountBuilderQuestTask(QuestHelperQuest quest){
        this.quest = quest;
    }

    public String getName() {
        return String.format("Quest - %s", quest.getName());
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && !isQuestCompleted(quest);
    }

    @Override
    public boolean isCompleted() {
        return super.isCompleted() || quest != null && isQuestCompleted(quest);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        questScript.shutdown();
        foodScript.shutdown();

        if (quest != null && !shutdown){
            sleepUntil(() -> Rs2Widget.getWidget(10027025) != null, 30_000);
            Rs2Widget.clickWidget(10027025);
        }
    }

    @Override
    public void run() {
        questScript.run(new MQuestConfig(){ });
        startupQuest();

        if (useFood){
            foodScript.run(new PlayerAssistConfig() {
                @Override
                public boolean toggleFood() {
                    return true;
                }
            });
        }

        super.run();
    }

    @Override
    public void tick() {
        var step = quest.getQuestHelper().getCurrentStep();
        if (step != null)
            currentStep = step;

        while (currentStep instanceof ConditionalStep)
            currentStep = currentStep.getActiveStep();

        if (currentStep instanceof ObjectStep)
            handleObjectStep((ObjectStep) currentStep);
        else if (currentStep instanceof NpcEmoteStep)
            handleNPCEmoteStep((NpcEmoteStep) currentStep);
        else if (currentStep instanceof NpcStep)
            handleNPCStep((NpcStep) currentStep);
        else if (currentStep instanceof DetailedQuestStep)
            handleDetailedStep((DetailedQuestStep) currentStep);
    }

    protected void handleObjectStep(ObjectStep step) { }
    protected void handleNPCEmoteStep(NpcEmoteStep step) { }
    protected void handleNPCStep(NpcStep step) { }
    protected void handleDetailedStep(DetailedQuestStep step) { }

    protected void startupQuest(){
        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().startUpQuest(quest.getQuestHelper()); return null; });
    }

    protected void stopQuest(){
        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().shutDownQuestFromSidebar(); return null; });

        if (ShortestPathPlugin.getMarker() != null)
            ShortestPathPlugin.exit();
    }

    protected boolean isQuestRunning(){
        return Microbot.getClientThread().runOnClientThread(() -> MQuestHelperPlugin.getSelectedQuest() != null);
    }

    protected MQuestHelperPlugin getQuestHelperPlugin() {
        return (MQuestHelperPlugin)Microbot.getPluginManager().getPlugins().stream().filter(x -> x instanceof MQuestHelperPlugin).findFirst().orElse(null);
    }
}
