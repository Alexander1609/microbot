package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.Arrays;

public class PrinceAliRescueTask extends AccountBuilderQuestTask {
    boolean inventoryCleared = false;
    boolean depositedWheat = false;

    public PrinceAliRescueTask(){
        super(QuestHelperQuest.PRINCE_ALI_RESCUE, false);
        memberOnly = false;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.CHANCELLOR_HASSAN && Rs2Player.getWorldLocation().distanceTo(new WorldArea(3121, 3240, 5, 4, 0)) == 0){
            Rs2GameObject.interact(Rs2GameObject.findDoor(2881));
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 10;
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("No. I think"))
            Rs2Widget.clickWidget("No. I think");
    }

    @Override
    public boolean doTaskPreparations() {
        var items = new int[]{ItemID.BRONZE_AXE, ItemID.BRONZE_PICKAXE, ItemID.SHEARS, ItemID.TINDERBOX};
        if (!Rs2Inventory.containsAll(items)
            || !Rs2Inventory.hasItemAmount("Coins", 120, true)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            if (!inventoryCleared){
                Rs2Bank.depositAll();
                inventoryCleared = true;
            }

            for (var item : items)
                if (!Rs2Inventory.contains(item))
                    Rs2Bank.withdrawItem(item);

            if (!Rs2Inventory.hasItemAmount("Coins", 200, true)){
                if (!Rs2Bank.hasBankItem("Coins", 200))
                    cancel();
                else
                    Rs2Bank.withdrawX("Coins", 200);
            }

            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.PINK_SKIRT)){
            if (!Rs2Walker.walkTo(new WorldPoint(3205, 3416, 0)))
                return false;

            if (!Rs2Shop.isOpen())
                Rs2Shop.openShop("Thessalia");
            else
                Rs2Shop.buyItem("Pink skirt", "1");

            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.BEER, 4)){
            if (!Rs2Walker.walkTo(new WorldPoint(3225, 3399, 0)))
                return false;

            if (!Rs2Dialogue.isInDialogue())
                Rs2Npc.interact("Bartender", "Talk-to");
            else if (Rs2Widget.hasWidget("continue"))
                Rs2Dialogue.clickContinue();
            else
                Rs2Widget.clickWidget("finest ale");

            return false;
        }

        if (!Rs2Inventory.contains(ItemID.CLAY, ItemID.SOFT_CLAY)){
            if (!Rs2Walker.walkTo(new WorldPoint(3178, 3369, 0)))
                return false;

            Rs2GameObject.interact("Clay rocks");
            Rs2Player.waitForAnimation();

            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.BALL_OF_WOOL, 3)
            && !Rs2Inventory.hasItemAmount(ItemID.WOOL, 3)){
            if (!Rs2Walker.walkTo(new WorldArea(3232, 3343, 15, 10, 0), 5))
                return false;

            var sheep = Rs2Npc.getNpcs("Sheep", true)
                    .filter(x -> Arrays.asList(Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getNpcDefinition(x.getId())).getActions()).contains(("Shear")))
                    .findFirst().orElse(null);

            if (sheep != null){
                Rs2Npc.interact(sheep, "Shear");
                Rs2Player.waitForAnimation();
            }

            return false;
        }

        if (!Rs2Inventory.contains(ItemID.BRONZE_BAR) && !Rs2Inventory.containsAll(ItemID.COPPER_ORE, ItemID.TIN_ORE)){
            if (!Rs2Walker.walkTo(new WorldPoint(3284, 3364, 0)))
                return false;

            if (!Rs2Inventory.contains(ItemID.COPPER_ORE))
                Rs2GameObject.interact("Copper rocks");
            else
                Rs2GameObject.interact("Tin rocks");

            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains(ItemID.REDBERRIES)){
            if (!Rs2Walker.walkTo(new WorldPoint(3275, 3369, 0), 3))
                return false;

            if (!Rs2GameObject.interact(23628, "pick"))
                Rs2GameObject.interact(23629, "pick");

            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains(ItemID.BRONZE_BAR)){
            if (!Rs2Walker.walkTo(new WorldPoint(3225, 3253, 0)))
                return false;

            Rs2GameObject.interact("Furnace", "Smelt", true);
            sleepUntil(() -> Rs2Widget.getWidget(17694734) != null, 2000);
            if (Rs2Widget.getWidget(17694734) != null)
                Rs2Widget.clickWidget(17694734);

            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.BALL_OF_WOOL, 3)){
            if (!Rs2Walker.walkTo(new WorldPoint(3209, 3213, 1), 1))
                return false;

            Rs2GameObject.interact("Spinning wheel", "Spin");
            sleepUntil(() -> Rs2Widget.getWidget(17694734) != null, 5000);
            Rs2Widget.clickWidget(17694734);

            sleep(5000);
            while (true) {
                var woolCount = Rs2Inventory.get("Ball of wool").quantity;
                sleep(3000);
                if (woolCount == Rs2Inventory.get("Ball of wool").quantity)
                    break;
            }

            return false;
        }

        if (!Rs2Inventory.contains("Bucket of water") && !Rs2Inventory.contains("Bucket")){
            if (!Rs2Walker.walkTo(new WorldPoint(3214, 9623, 0)))
                return false;

            if (!Rs2GroundItem.exists("Bucket", 10)){
                Rs2Player.logout();
                return false;
            }

            Rs2GroundItem.loot("Bucket", 10);
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains("Pot of flour") && !Rs2Inventory.contains("Pot")){
            if (!Rs2Walker.walkTo(new WorldPoint(3208, 3214, 0)))
                return false;

            if (!Rs2GroundItem.exists("Pot", 10)){
                Rs2Player.logout();
                return false;
            }

            Rs2GroundItem.loot("Pot", 10);
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.containsAll(ItemID.SOFT_CLAY, ItemID.BUCKET_OF_WATER)){
            if (!Rs2Inventory.contains(ItemID.BUCKET_OF_WATER)){
                Rs2Inventory.use(ItemID.BUCKET);
                Rs2GameObject.interact("Sink");
            } else {
                Rs2Inventory.combine(ItemID.BUCKET_OF_WATER, ItemID.CLAY);
            }

            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.ONION, 2) && !Rs2Inventory.contains(ItemID.YELLOW_DYE)){
            if (!Rs2Walker.walkTo(new WorldPoint(3189, 3267, 0), 3))
                return false;

            Rs2GameObject.interact("Onion", "pick");
            Rs2Player.waitForAnimation();
            return false;
        }

        if (!Rs2Inventory.contains("Pot of flour")){
            if (!Rs2Inventory.contains("Grain") && !depositedWheat){
                if (!Rs2Walker.walkTo(new WorldPoint(3161, 3293, 0), 2))
                    return false;

                Rs2GameObject.interact("Wheat", "Pick");
                Rs2Player.waitForAnimation();
                return false;
            }

            if (!depositedWheat){
                if (!Rs2Walker.walkTo(new WorldPoint(3166, 3306, 2)))
                    return false;

                if (Rs2Inventory.contains("Grain")){
                    Rs2GameObject.interact(24961, "Fill");
                    Rs2Player.waitForWalking();
                    Rs2Player.waitForAnimation();

                    Rs2GameObject.interact("Hopper controls", "Operate");
                    Rs2Player.waitForWalking();
                    Rs2Player.waitForAnimation();

                    if (!Rs2Inventory.contains("Grain"))
                        depositedWheat = true;
                }
            }

            if (!Rs2Walker.walkTo(new WorldPoint(3166, 3306, 0)))
                return false;

            Rs2GameObject.interact(1781, "Empty");
            Rs2Player.waitForAnimation();

            return false;
        }

        if (!Rs2Inventory.contains(ItemID.ASHES)){
            if (!Rs2Walker.walkTo(new WorldPoint(3163, 3282, 0)))
                return false;

            if (Rs2GroundItem.loot(ItemID.ASHES)
                    || Rs2GameObject.findObject("fire", true, 100, false, Rs2Player.getWorldLocation()) != null)
                return false;
            else if (!Rs2Inventory.contains(ItemID.LOGS)) {
                Rs2GameObject.interact("Tree", true);
                Rs2Player.waitForAnimation();
            } else {
                Rs2Inventory.combine(ItemID.TINDERBOX, ItemID.LOGS);
                Rs2Player.waitForAnimation();
            }

            return false;
        }

        if (!Rs2Inventory.contains(ItemID.ROPE)) {
            if (!Rs2Walker.walkTo(new WorldPoint(3098, 3258, 0)))
                return false;

            if (!Rs2Shop.isOpen())
                Rs2Shop.openShop("Ned");
            else
                Rs2Shop.buyItem("Rope", "1");

            return false;
        }

        if (!Rs2Inventory.hasItem(ItemID.YELLOW_DYE)){
            if (!Rs2Walker.walkTo(new WorldPoint(3085, 3258, 0), 3))
                return false;

            if (!Rs2Dialogue.isInDialogue()){
                Rs2Npc.interact(NpcID.AGGIE);
                sleepUntil(Rs2Dialogue::isInDialogue, 5000);
            } else if (Rs2Widget.hasWidget("Click here to continue"))
                Rs2Dialogue.clickContinue();
            else if (Rs2Widget.hasWidget("Can you make dyes for me please?"))
                Rs2Widget.clickWidget("Can you make dyes for me please?");
            else if (Rs2Widget.hasWidget("yellow dye"))
                Rs2Widget.clickWidget("yellow dye");

            return false;
        }

        return true;
    }
}
