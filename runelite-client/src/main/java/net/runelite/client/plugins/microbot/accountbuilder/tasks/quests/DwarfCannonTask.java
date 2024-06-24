package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class DwarfCannonTask extends AccountBuilderQuestTask {
    public DwarfCannonTask(){
        super(QuestHelperQuest.DWARF_CANNON);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20;
    }

    @Override
    public void init() {
        super.init();

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(
                new Restriction(2563, 3456, 0),
                new Restriction(2569, 3456, 0),
                new Restriction(2569, 3455, 0),
                new Restriction(2556, 3468, 0),
                new Restriction(2558, 3458, 0),
                new Restriction(2554, 3479, 0),
                new Restriction(2566, 3455, 0),
                new Restriction(2566, 3456, 0),
                new Restriction(2573, 3456, 0),
                new Restriction(2577, 3456, 0)
        );
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.getText().contains("Inspect the railings to fix them.")){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                return;

            sleepUntil(() -> Rs2Widget.hasWidget("continue"), 500);
            if (!Rs2Dialogue.isInDialogue())
                Rs2GameObject.interact(step.objectID);

            sleepUntil(() -> Rs2Widget.hasWidget("continue"), 5000);
            Rs2Dialogue.clickContinue();
            Rs2Player.waitForAnimation();

            if (!isQuestRunning())
                startupQuest();
        } else if (step.objectID == NullObjectID.NULL_15597){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                return;

            Rs2Inventory.useItemOnObject(ItemID.TOOLKIT, NullObjectID.NULL_15597);
            sleepUntil(() -> Rs2Widget.isWidgetVisible(409, 0), 5000);

            Rs2Widget.clickWidget(409, 3);
            Rs2Widget.clickWidget(409, 8);
            sleep(500, 1000);

            Rs2Widget.clickWidget(409, 2);
            Rs2Widget.clickWidget(409, 7);
            sleep(500, 1000);

            Rs2Widget.clickWidget(409, 1);
            Rs2Widget.clickWidget(409, 9);
            sleep(500, 1000);

            if (!isQuestRunning())
                startupQuest();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Inventory.hasItemAmount("Coins", 200)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            var amount = Random.random(200, 300);
            if (!Rs2Bank.hasBankItem("Coins", amount)){
                cancel();
                return false;
            }

            Rs2Bank.withdrawX("Coins", amount);
        }

        return true;
    }
}
