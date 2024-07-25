package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

import java.util.stream.Collectors;

public class EnterTheAbyssTask extends AccountBuilderQuestTask {
    public EnterTheAbyssTask(){
        super(QuestHelperQuest.ENTER_THE_ABYSS, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("Enter Wilderness"))
            Rs2Widget.clickWidget("Enter Wilderness");

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2876, 4802, 62, 62, 0)) == 0
                && (Rs2Npc.getNpc("Portal", false) != null
                    || Rs2GameObject.findObjectById(34779) != null)){
            stopQuest();
            Rs2Walker.setTarget(null);
            var npc = Rs2Npc.getNpc("Portal", false);
            var go = Rs2GameObject.findObjectById(34779);
            if (npc != null)
                Rs2Npc.interact(npc, "Use");
            else if (go != null)
                Rs2GameObject.interact(go, "Exit");
            Rs2Player.waitForWalking();
            startupQuest();
        }
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
