package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.Arrays;

public class SheepShearerTask extends AccountBuilderQuestTask {
    public SheepShearerTask(){
        super(QuestHelperQuest.SHEEP_SHEARER, false);
        memberOnly = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Dialogue.isInDialogue() && Rs2Widget.hasWidget("Climb down"))
            Rs2Widget.clickWidget("Climb down");
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.SPINNING_WHEEL_14889){
            if (Rs2Player.isAnimating() && isQuestRunning())
                stopQuest();

            if (!Rs2Inventory.contains(ItemID.WOOL) && !isQuestRunning())
                startupQuest();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 21){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        return true;
    }
}
