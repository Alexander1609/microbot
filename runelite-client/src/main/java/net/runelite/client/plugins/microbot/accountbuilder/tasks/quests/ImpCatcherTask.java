package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.QuestHelperQuest;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ImpCatcherTask extends AccountBuilderQuestTask {
    long lastImpTime = Long.MAX_VALUE;

    public ImpCatcherTask(){
        super(QuestHelperQuest.IMP_CATCHER);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Equipment.isWearing("Mithril longsword")){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawAndEquip("Mithril longsword");
            return false;
        }

        if (Rs2Inventory.count("bead") + Rs2Inventory.getEmptySlots() < 28){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        if (!Rs2Inventory.contains(ItemID.BLACK_BEAD)
            || !Rs2Inventory.contains(ItemID.RED_BEAD)
            || !Rs2Inventory.contains(ItemID.WHITE_BEAD)
            || !Rs2Inventory.contains(ItemID.YELLOW_BEAD)){
            if (Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(Rs2GroundItem.getAll(20)).anyMatch(x -> x.getItem().getName().contains("bead")))){
                if (ShortestPathPlugin.getMarker() != null)
                    ShortestPathPlugin.exit();

                if (Rs2GroundItem.lootItemsBasedOnNames(new LootingParameters(
                        50,
                        1,
                        1,
                        1,
                        false,
                        true,
                        "bead"
                ))){
                    Microbot.pauseAllScripts = false;
                    return false;
                }
            }

            if (!Rs2Player.isInteracting()
                && !Rs2Combat.inCombat()
                && !Rs2Player.isWalking()
                && !Rs2Player.isInteracting()){
                if (!Rs2Walker.walkTo(new WorldPoint(3006, 3315, 0)))
                    return false;

                var imp = Rs2Npc.getNpcs("Imp").filter(Rs2Npc::hasLineOfSight).findFirst().orElse(null);
                if (imp != null){
                    Rs2Npc.interact(imp, "Attack");
                    lastImpTime = System.currentTimeMillis();
                }
            }

            if (lastImpTime + TimeUnit.MINUTES.toMillis(1) < System.currentTimeMillis()){
                lastImpTime = System.currentTimeMillis();
                Rs2Player.logout();
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() > 30;
    }
}
