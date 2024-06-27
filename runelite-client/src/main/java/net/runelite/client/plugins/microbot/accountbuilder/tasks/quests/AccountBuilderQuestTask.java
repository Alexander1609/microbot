package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.GrandExchangeOfferState;
import net.runelite.api.MenuAction;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
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
import net.runelite.client.plugins.questhelper.requirements.player.CombatLevelRequirement;
import net.runelite.client.plugins.questhelper.requirements.quest.QuestPointRequirement;
import net.runelite.client.plugins.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.questhelper.steps.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AccountBuilderQuestTask extends AccountBuilderTask {
    private final QuestHelperQuest quest;
    private final MQuestScript questScript  = new MQuestScript();
    private final FoodScript foodScript = new FoodScript();

    @Getter
    protected QuestStep currentStep;

    protected boolean useFood = false;
    protected boolean inventoryCleared = false;
    protected ArrayList<Integer> itemsBought = new ArrayList<>();

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
        if (event.getGameState() == GameState.LOGGED_IN){
            if (!isQuestRunning()){
                startupQuest();
            }
        }
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

        for (var requirement : requirements){
            if (!Microbot.getClientThread().runOnClientThread(() -> requirement.check(Microbot.getClient())))
                return false;
        }

        return true;
    }

    protected boolean clearInventory(Integer... except){
        if (inventoryCleared || Rs2Inventory.isEmpty())
            return true;

        if (!Rs2Bank.walkToBankAndUseBank())
            return false;

        if (except.length > 0)
            inventoryCleared = Rs2Bank.depositAllExcept(except);
        else{
            Rs2Bank.depositAll();
            if (Rs2Inventory.count() == 0)
                inventoryCleared = true;
        }
        return inventoryCleared;
    }

    protected boolean withdrawBuyRequiredItems(){
        var itemRequirements = quest.getQuestHelper().getItemRequirements();

        if (Rs2Bank.bankItems == null || Rs2Bank.bankItems.isEmpty()){
            Rs2Bank.walkToBankAndUseBank();
            return false;
        }

        var itemsToBuy = itemRequirements.stream().filter(x -> !itemsBought.contains(x.getId())
                && !Rs2Inventory.hasItemAmount(x.getId(), x.getQuantity())
                && !Rs2Bank.hasBankItem(x.getId(), x.getQuantity())
                && !x.getAllIds().stream().anyMatch(y -> Rs2Inventory.hasItemAmount(y, x.getQuantity()) || Rs2Bank.hasBankItem(y, x.getQuantity()))).collect(Collectors.toList());
        if (!itemsToBuy.isEmpty()){
            if (!Rs2GrandExchange.walkToGrandExchange() || !Rs2GrandExchange.openExchange())
                return false;

            var clicks = 4;
            boolean bought = false;
            while (!bought){
                Rs2GrandExchange.buyItemAboveXPercent(itemsToBuy.get(0).getName(), itemsToBuy.get(0).getQuantity(), 2, clicks);
                sleepUntil(() -> Rs2GrandExchange.hasBoughtOffer(itemsToBuy.get(0).getId()), 10000);

                if (Rs2GrandExchange.hasBoughtOffer(itemsToBuy.get(0).getId())){
                    Rs2GrandExchange.collectToBank();
                    bought = true;
                    itemsBought.add(itemsToBuy.get(0).getId());
                } else {
                    var slot = Arrays.stream(GrandExchangeSlots.values()).filter(x -> Arrays.stream(Rs2GrandExchange.getSlot(x).getDynamicChildren()).anyMatch(y -> y.getItemId() == itemsToBuy.get(0).getId())).findFirst().orElse(null);
                    var slotWidget = Rs2GrandExchange.getSlot(slot);
                    Microbot.doInvoke(new NewMenuEntry(2, slotWidget.getId(), MenuAction.CC_OP.getId(), 2, -1, "Abort offer"), slotWidget.getBounds());
                    sleep(100, 200);
                    Rs2GrandExchange.collectToBank();
                    sleep(100, 200);

                    clicks *= 2;
                }
            }
            return false;
        }

        for (var item : itemRequirements){
            var id = item.getId();
            if (!Rs2Bank.hasBankItem(id, item.getQuantity()) && !Rs2Inventory.hasItemAmount(id, item.getQuantity()))
                id = item.getAllIds().stream().filter(x -> Rs2Inventory.hasItemAmount(x, item.getQuantity()) || Rs2Bank.hasBankItem(x, item.getQuantity())).findFirst().orElse(-1);

            if (Rs2Inventory.hasItemAmount(id, item.getQuantity()))
                continue;

            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            var invItem = Rs2Inventory.get(id);
            if (invItem == null || !Rs2Inventory.hasItemAmount(id, item.getQuantity(), invItem.isStackable())){
                if (item.getQuantity() == 1)
                    Rs2Bank.withdrawItem(id);
                else
                    Rs2Bank.withdrawX(id, item.getQuantity() - Rs2Inventory.count(id));
                return false;
            }
        }

        return true;
    }
}
