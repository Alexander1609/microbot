package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class ErnestTheChickenTask extends AccountBuilderQuestTask {
    public ErnestTheChickenTask(){
        super(QuestHelperQuest.ERNEST_THE_CHICKEN);
        useFood = true;
        memberOnly = false;
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.FOUNTAIN){
            if (Rs2Player.getWorldLocation().distanceTo(step.getWorldPoint()) < 5){
                Rs2Inventory.use(ItemID.POISONED_FISH_FOOD);
                Rs2GameObject.interact(ObjectID.FOUNTAIN);
            }
        } else if (step.objectID == NullObjectID.NULL_149){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3105, 9758, 8, 10, 0)) > 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9745, 19, 13, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_144);
        } else if (step.objectID == NullObjectID.NULL_147){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3105, 9758, 8, 10, 0)) == 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9745, 19, 13, 0)) > 0)
                Rs2GameObject.interact(NullObjectID.NULL_144);
        } else if (step.objectID == NullObjectID.NULL_150){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9758, 5, 5, 0)) > 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9745, 19, 13, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_145);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9758, 5, 5, 0)) == 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3096, 9758, 4, 5, 0)) > 0)
                Rs2GameObject.interact(NullObjectID.NULL_140);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3096, 9758, 4, 5, 0)) == 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3096, 9763, 4, 5, 0)) > 0)
                Rs2GameObject.interact(NullObjectID.NULL_143);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9763, 5, 5, 0)) > 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3105, 9758, 8, 10, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_137);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3096, 9763, 4, 5, 0)) > 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9763, 5, 5, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_138);
        } else if (step.objectID == NullObjectID.NULL_148){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3096, 9763, 4, 5, 0)) == 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9763, 5, 5, 0)) > 0)
                Rs2GameObject.interact(NullObjectID.NULL_138);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9763, 5, 5, 0)) == 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3105, 9758, 8, 10, 0)) > 0)
                Rs2GameObject.interact(NullObjectID.NULL_137);
        } else if (step.objectID == ObjectID.LADDER_132){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3090, 9753, 10, 5, 0)) == 0
                    && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9745, 19, 13, 0)) > 0)
                Rs2GameObject.interact(NullObjectID.NULL_141);
        }
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("oil can"))){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3096, 9763, 4, 5, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_138);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9763, 5, 5, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_142);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9758, 5, 5, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_145);
            else if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3100, 9745, 19, 13, 0)) == 0)
                Rs2GameObject.interact(NullObjectID.NULL_141);
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 5 || Rs2Inventory.getInventoryFood().size() < 10){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank())
                return false;

            Rs2Bank.depositAll();
            if (Rs2Bank.hasBankItem("Shrimps", 10))
                Rs2Bank.withdrawX("Shrimps", 10);
            else
                cancel();

            return false;
        }

        return true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 20;
    }
}
