package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.awt.*;

public class TouristTrapTask extends AccountBuilderQuestTask {
    public TouristTrapTask(){
        super(QuestHelperQuest.THE_TOURIST_TRAP);
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("Proceed regardless"))
            Rs2Widget.clickWidget("Proceed regardless");

        if (Rs2Widget.hasWidget("Yes, I can take him on!"))
            Rs2Npc.interact(NpcID.MERCENARY_CAPTAIN, "Talk-to");
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.GATE_2673){
            if (isQuestRunning() && Rs2Equipment.isWearing("Mithril platelegs")){
                stopQuest();
                Rs2Walker.setTarget(null);
                Rs2Walker.walkFastCanvas(Rs2Player.getWorldLocation());
            }

            if (Rs2Equipment.isWearing("Mithril longsword"))
                Microbot.doInvoke(new NewMenuEntry(-1, 25362450, MenuAction.CC_OP.getId(), 1, -1, "<col=ff9040>Glarial's amulet</col>"), new Rectangle(0, 0, 1, 1));
            else if (Rs2Equipment.isWearing("Mithril chainbody"))
                Microbot.doInvoke(new NewMenuEntry(-1, 25362451, MenuAction.CC_OP.getId(), 1, -1, "<col=ff9040>Glarial's amulet</col>"), new Rectangle(0, 0, 1, 1));
            else if (Rs2Equipment.isWearing("Mithril full helm"))
                Microbot.doInvoke(new NewMenuEntry(-1, 25362447, MenuAction.CC_OP.getId(), 1, -1, "<col=ff9040>Glarial's amulet</col>"), new Rectangle(0, 0, 1, 1));
            else if (Rs2Equipment.isWearing("Mithril kiteshield"))
                Microbot.doInvoke(new NewMenuEntry(-1, 25362452, MenuAction.CC_OP.getId(), 1, -1, "<col=ff9040>Glarial's amulet</col>"), new Rectangle(0, 0, 1, 1));
            else if (Rs2Equipment.isWearing("Mithril platelegs"))
                Microbot.doInvoke(new NewMenuEntry(-1, 25362453, MenuAction.CC_OP.getId(), 1, -1, "<col=ff9040>Glarial's amulet</col>"), new Rectangle(0, 0, 1, 1));
            else if (!isQuestRunning())
                startupQuest();
        } else if (step.objectID == ObjectID.MINE_CART_2684 && !Rs2Dialogue.isInDialogue() && step.getText().stream().anyMatch(x -> x.contains("Return in the mine cart you came in on."))){
            Rs2GameObject.interact(ObjectID.MINE_CART_2684, "Search");
            Rs2Player.waitForWalking();
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.MINE_CART_DRIVER && Rs2Widget.hasWidget("Hurry up, get in the cart")){
            stopQuest();
            Rs2GameObject.interact(NullObjectID.NULL_2682, "Search");
            Rs2Player.waitForAnimation();
            startupQuest();
        } else if (step.npcID == NpcID.IRENA && step.getText().stream().anyMatch(x -> x.contains("finish the quest"))){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Dialogue.isInDialogue()){
                Rs2Npc.interact(NpcID.IRENA, "Talk-to");
                Rs2Player.waitForWalking();
            } else if (Rs2Widget.hasWidget("Agility"))
                Rs2Widget.clickWidget("Agility");
            else if (Rs2Dialogue.hasContinue())
                Rs2Dialogue.clickContinue();
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 45
                && isQuestCompleted(QuestHelperQuest.PRINCE_ALI_RESCUE);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory())
            return false;

        Rs2Bank.withdrawAndEquip("Mithril longsword");
        Rs2Bank.withdrawAndEquip("Mithril chainbody");
        Rs2Bank.withdrawAndEquip("Mithril full helm");
        Rs2Bank.withdrawAndEquip("Mithril kiteshield");
        Rs2Bank.withdrawAndEquip("Mithril platelegs");

        return clearInventory() && withdrawBuyRequiredItems(
                new ItemRequirement("Waterskin(4)", ItemID.WATERSKIN4, 5),
                new ItemRequirement("Bronze pickaxe", ItemID.BRONZE_PICKAXE, 1),
                new ItemRequirement("Shantay pass", ItemID.SHANTAY_PASS, 1)
        );
    }
}
