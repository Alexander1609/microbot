package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.AntiPoisonScript;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.quest.MQuestConfig;
import net.runelite.client.plugins.microbot.quest.MQuestScript;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirements;
import net.runelite.client.plugins.questhelper.requirements.player.CombatLevelRequirement;
import net.runelite.client.plugins.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.questhelper.steps.*;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class AccountBuilderQuestTask extends AccountBuilderTask {
    @Getter
    private final QuestHelperQuest quest;
    private final MQuestScript questScript  = new MQuestScript();
    ReentrantLock questScriptLock = new ReentrantLock();
    private final FoodScript foodScript = new FoodScript();
    private final AntiPoisonScript antiPoisonScript = new AntiPoisonScript();

    @Getter
    protected QuestStep currentStep;

    protected boolean useFood = false;

    protected boolean useAntiPoison = false;

    public AccountBuilderQuestTask(QuestHelperQuest quest, ItemRequirement... additionalRequirements){
        this(quest, true, additionalRequirements);
    }

    public AccountBuilderQuestTask(QuestHelperQuest quest, boolean buyItems, ItemRequirement... additionalRequirements){
        this.quest = quest;

        itemRequirements.addAll(List.of(additionalRequirements));

        if (buyItems && quest != null && quest.getQuestHelper().getItemRequirements() != null){
            for (var requirement : quest.getQuestHelper().getItemRequirements()){
                if (requirement instanceof ItemRequirements){
                    var requirements = (ItemRequirements)requirement;

                    if (requirements.getLogicType() == LogicType.AND)
                        itemRequirements.addAll(requirements.getItemRequirements());
                    else if (requirements.getLogicType() == LogicType.OR){
                        var owned = requirements.getItemRequirements().stream().filter(x ->
                                x.getAllIds().stream().anyMatch(y -> Rs2Inventory.hasItemAmount(y, x.getQuantity())
                                    || x.getQuantity() == 1 && Rs2Equipment.isWearing(y)
                                    || Rs2Bank.hasBankItem(y, x.getQuantity())))
                                .findFirst().orElse(null);
                        if (owned != null)
                            itemRequirements.add(owned);
                        else{
                            var itemPriceMap = requirements.getItemRequirements().stream().collect(Collectors.toMap(x -> x, x ->
                                                x.getAllIds().stream().collect(Collectors.toMap(y -> y, y -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemPriceWithSource(y, false))))
                                                .entrySet().stream().filter(y -> y.getValue() > 0).min(Map.Entry.comparingByValue()).orElseGet(() -> new AbstractMap.SimpleEntry<>(-1, -1)).getKey()));

                            itemRequirements.add(itemPriceMap.entrySet().stream().filter(x -> x.getValue() > 0).min(Map.Entry.comparingByValue()).get().getKey());
                        }
                    } else
                        throw new UnsupportedOperationException();

                    continue;
                }

                itemRequirements.add(requirement.copy());
            }
        }

        // Increase coin requirement for possible ship transports
        var coinRequirement = itemRequirements.stream().filter(x -> x.getName().equals("Coins")).findFirst().orElse(null);
        if (coinRequirement != null) {
            coinRequirement.setQuantity(coinRequirement.getQuantity() + Random.random(200, 300));
        }
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

        stopQuest();
        foodScript.shutdown();
        antiPoisonScript.shutdown();

        if (quest != null && !shutdown){
            sleepUntil(() -> Rs2Widget.getWidget(10027025) != null, 30_000);
            Rs2Widget.clickWidget(10027025);
        }
    }

    @Override
    public void run() {
        startupQuest();

        Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().startUpQuest(quest.getQuestHelper()); return null; });

        if (useFood){
            foodScript.run(new PlayerAssistConfig() {
                @Override
                public boolean toggleFood() {
                    return true;
                }
            });
        }

        if (useAntiPoison){
            antiPoisonScript.run(new PlayerAssistConfig() {
                @Override
                public boolean useAntiPoison() {
                    return true;
                }
            });
        }

        super.run();
    }

    @Override
    public void tick() {
        var step = quest.getQuestHelper().getCurrentStep();
        var prevStep = currentStep;
        if (step != null)
            currentStep = step;

        while (currentStep instanceof ConditionalStep)
            currentStep = currentStep.getActiveStep();

        if (prevStep != currentStep && !isQuestRunning()) {
            startupQuest();
            blockStuckPrevention = false;
        }

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
            if (Microbot.getClientThread().runOnClientThread(() -> getQuestHelperPlugin().getSelectedQuest() == null)){
                Microbot.getClientThread().runOnClientThread(() -> { getQuestHelperPlugin().startUpQuest(quest.getQuestHelper()); return null; });
            }
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        questScript.onChatMessage(chatMessage);
    }

    protected void startupQuest() {
        if (!Global.sleepUntilTrue(() ->  questScriptLock.tryLock(), 100, 2000))
            return;

        if (!questScript.isRunning())
            questScript.run(new MQuestConfig(){ });

        questScriptLock.unlock();
    }

    protected void stopQuest(){
        if (!Global.sleepUntilTrue(() ->  questScriptLock.tryLock(), 100, 2000))
            return;

        questScript.shutdown();

        questScriptLock.unlock();
    }

    public boolean isQuestRunning(){
        return questScript.isRunning();
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

        var recommended = quest.getQuestHelper().getGeneralRecommended();
        if (recommended != null){
            for (var requirement : recommended){
                if (requirement instanceof CombatLevelRequirement
                        && !Microbot.getClientThread().runOnClientThread(() -> requirement.check(Microbot.getClient())))
                    return false;
            }
        }

        return true;
    }

    @Override
    protected boolean withdrawBuyItems() {
        var success = super.withdrawBuyItems();

        if (!success)
            return false;

        var recommendedItems = quest.getQuestHelper().getItemRecommended();
        if (recommendedItems != null){

            for (var item : recommendedItems){
                if (item.getId() == -1) continue;

                var amount = item.getQuantity();
                if (ItemCollections.COINS.getItems().contains(item.getId())){
                    amount += Random.random(200, 400);
                }

                List<Integer> itemIds = null;
                if (item.getId() == ItemID.ARDOUGNE_TELEPORT)
                    itemIds = ItemCollections.ARDY_CLOAKS.getItems();

                if (itemIds == null)
                    itemIds = item.getAllIds();

                int finalAmount = amount;
                if (amount == 1 && itemIds.stream().anyMatch(Rs2Equipment::isWearing)
                        || itemIds.stream().anyMatch(x -> Rs2Inventory.hasItemAmount(x, finalAmount)))
                    continue;

                for (var itemId : itemIds){
                    if (Rs2Bank.hasBankItem(itemId, amount)){
                        if (!Rs2Bank.walkToBankAndUseBank())
                            return false;

                        Rs2Bank.withdrawX(itemId, amount);
                        sleep(100, 200);
                        break;
                    }
                }
            }
        }

        return success;
    }
}
