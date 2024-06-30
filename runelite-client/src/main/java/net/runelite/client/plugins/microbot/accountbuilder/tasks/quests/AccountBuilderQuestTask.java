package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.globval.WidgetIndices;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.quest.MQuestConfig;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeSlots;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.requirements.player.CombatLevelRequirement;
import net.runelite.client.plugins.questhelper.requirements.quest.QuestPointRequirement;
import net.runelite.client.plugins.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.questhelper.steps.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AccountBuilderQuestTask extends AccountBuilderTask {
    @Getter
    private final QuestHelperQuest quest;
    private final MQuestScript questScript  = new MQuestScript();
    private final FoodScript foodScript = new FoodScript();

    @Getter
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
        return super.requirementsMet() && !isQuestCompleted(quest) && checkRequirements();
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

    @Override
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN && scheduledFuture != null){
            if (!isQuestRunning()){
                startupQuest();
            }
        }
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        questScript.onChatMessage(chatMessage);
    }

    protected void startupQuest(){
        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().startUpQuest(quest.getQuestHelper()); return null; });
    }

    protected void stopQuest(){
        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().shutDownQuestFromSidebar(); return null; });

        if (ShortestPathPlugin.getMarker() != null)
            ShortestPathPlugin.exit();
    }

    public boolean isQuestRunning(){
        return Microbot.getClientThread().runOnClientThread(() -> getQuestHelperPlugin().getSelectedQuest() != null);
    }

    protected QuestHelperPlugin getQuestHelperPlugin() {
        return (QuestHelperPlugin)Microbot.getPluginManager().getPlugins().stream().filter(x -> x instanceof QuestHelperPlugin).findFirst().orElse(null);
    }

    protected boolean checkRequirements(){
        var requirements = quest.getQuestHelper().getGeneralRequirements();

        if (requirements == null)
            return true;

        for (var requirement : requirements){
            if (!Microbot.getClientThread().runOnClientThread(() -> requirement.check(Microbot.getClient())))
                return false;
        }

        return true;
    }

    private List<ItemRequirement> getAllItemRequirements(ItemRequirement... additionalItems){
        var combinedRequirements = Stream.concat(Arrays.stream(additionalItems), quest.getQuestHelper().getItemRequirements().stream().map(ItemRequirement::copy)).collect(Collectors.toList());

        // Take more coins for possible transports
        var coinRequirement = combinedRequirements.stream().filter(x -> x.getName().equals("Coins")).findFirst().orElse(null);
        if (coinRequirement != null) {
            coinRequirement.setQuantity(coinRequirement.getQuantity() + 300);
        }

        return combinedRequirements;
    }

    protected boolean withdrawBuyRequiredItems(){
        return withdrawBuyItems(getAllItemRequirements());
    }

    protected boolean withdrawBuyRequiredItems(ItemRequirement... additionalItems){
        return withdrawBuyItems(getAllItemRequirements(additionalItems));
    }
}
