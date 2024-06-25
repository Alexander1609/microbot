package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class GoblinDiplomacyTask extends AccountBuilderQuestTask {
    public GoblinDiplomacyTask(){
        super(QuestHelperQuest.GOBLIN_DIPLOMACY);
        memberOnly = false;
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 15){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        if (!Rs2Inventory.hasItemAmount("Coins", 35, true)
                && !Rs2Inventory.hasItem(ItemID.WOAD_LEAF)
                && !Rs2Inventory.hasItem(ItemID.BLUE_DYE)
                && !Rs2Inventory.hasItem(ItemID.BLUE_GOBLIN_MAIL)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            if (!Rs2Bank.hasBankItem("Coins", 35)){
                cancel();
                return false;
            }

            Rs2Bank.withdrawX("Coins", 35);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount("goblin mail", 3)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            if (!Rs2Bank.hasBankItem("goblin mail", 3)){
                cancel();
                return false;
            }

            Rs2Bank.withdrawItem("goblin mail");
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.ORANGE_GOBLIN_MAIL)
                && !Rs2Inventory.hasItem(ItemID.ORANGE_DYE)
                && !Rs2Inventory.hasItem(ItemID.YELLOW_DYE)
                && !Rs2Inventory.hasItemAmount(ItemID.ONION, 2)){
            if (!Rs2Walker.walkTo(new WorldPoint(3189, 3267, 0), 3))
                return false;

            Rs2GameObject.interact("Onion", "pick");
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.ORANGE_GOBLIN_MAIL)
                && !Rs2Inventory.hasItem(ItemID.ORANGE_DYE)
                && !Rs2Inventory.hasItem(ItemID.RED_DYE)
                && !Rs2Inventory.hasItemAmount(ItemID.REDBERRIES, 3)){
            if (!Rs2Walker.walkTo(new WorldPoint(3275, 3369, 0), 3))
                return false;

            if (!Rs2GameObject.interact(23628, "pick"))
                Rs2GameObject.interact(23629, "pick");

            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.BLUE_GOBLIN_MAIL)
                && !Rs2Inventory.hasItem(ItemID.BLUE_DYE)
                && !Rs2Inventory.hasItemAmount(ItemID.WOAD_LEAF, 2)){
            if (!Rs2Walker.walkTo(new WorldPoint(3025, 3379, 0), 5))
                return false;

            if (!Rs2Dialogue.isInDialogue()){
                Rs2Npc.interact(NpcID.WYSON_THE_GARDENER);
                sleepUntil(Rs2Dialogue::isInDialogue, 5000);
            } else if (Rs2Widget.hasWidget("Click here to continue"))
                Rs2Dialogue.clickContinue();
            else if (Rs2Widget.hasWidget("I need woad leaves"))
                Rs2Widget.clickWidget("I need woad leaves");
            else if (Rs2Widget.hasWidget("How about 20 coins?"))
                Rs2Widget.clickWidget("How about 20 coins?");

            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.BLUE_GOBLIN_MAIL)
                && !Rs2Inventory.hasItem(ItemID.ORANGE_GOBLIN_MAIL)
                && !Rs2Inventory.hasItem(ItemID.ORANGE_DYE)
                && (!Rs2Inventory.hasItem(ItemID.BLUE_DYE)
                    || !Rs2Inventory.hasItem(ItemID.YELLOW_DYE)
                    || !Rs2Inventory.hasItem(ItemID.RED_DYE))){
            if (!Rs2Walker.walkTo(new WorldPoint(3085, 3258, 0), 3))
                return false;

            if (!Rs2Dialogue.isInDialogue()){
                Rs2Npc.interact(NpcID.AGGIE);
                sleepUntil(Rs2Dialogue::isInDialogue, 5000);
            } else if (Rs2Widget.hasWidget("Click here to continue"))
                Rs2Dialogue.clickContinue();
            else if (Rs2Widget.hasWidget("Can you make dyes for me please?"))
                Rs2Widget.clickWidget("Can you make dyes for me please?");
            else if (!Rs2Inventory.hasItem(ItemID.BLUE_DYE) && Rs2Widget.hasWidget("blue dye"))
                Rs2Widget.clickWidget("blue dye");
            else if (!Rs2Inventory.hasItem(ItemID.YELLOW_DYE) && Rs2Widget.hasWidget("yellow dye"))
                Rs2Widget.clickWidget("yellow dye");
            else if (!Rs2Inventory.hasItem(ItemID.RED_DYE) && Rs2Widget.hasWidget("red dye"))
                Rs2Widget.clickWidget("red dye");

            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.ORANGE_GOBLIN_MAIL) && !Rs2Inventory.hasItem(ItemID.ORANGE_DYE)){
            Rs2Inventory.combine(ItemID.YELLOW_DYE, ItemID.RED_DYE);
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.ORANGE_GOBLIN_MAIL)){
            Rs2Inventory.combine(ItemID.ORANGE_DYE, ItemID.GOBLIN_MAIL);
            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.BLUE_GOBLIN_MAIL)){
            Rs2Inventory.combine(ItemID.BLUE_DYE, ItemID.GOBLIN_MAIL);
            return false;
        }

        return true;
    }
}
