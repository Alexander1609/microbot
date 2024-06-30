package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

public class EnterTheAbyssTask extends AccountBuilderQuestTask {
    public EnterTheAbyssTask(){
        super(QuestHelperQuest.ENTER_THE_ABYSS);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("Enter Wilderness"))
            Rs2Widget.clickWidget("Enter Wilderness");

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2877, 4802, 64, 63, 0)) == 0){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(new WorldPoint(2866, 4580, 0)))
                return;

            Rs2GameObject.interact("Portal");
            Rs2Player.waitForWalking();
        } else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == 11432 && step.allIds().size() == 1)
            step.addAlternateNpcs(11433);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory())
            return false;

        if (!Rs2Bank.isOpen()){
            Rs2Bank.walkToBankAndUseBank();
            return false;
        }

        Rs2Bank.depositEquipment();
        return true;
    }
}
