package net.runelite.client.plugins.microbot.accountbuilder.tasks.helper.equipment;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class EquipmentHelper {
    public static boolean getBestSkillingEquipment(Skill skill){
        var items = Arrays.stream(SkillingEquipment.values())
                .filter(x -> x.getSkill() == skill
                    && Microbot.getClient().getRealSkillLevel(skill) >= x.getLevel())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        while (Rs2Bank.bankItems.isEmpty() && (!Rs2Bank.walkToBank() || !Rs2Bank.openBank()))
            sleep(1000, 2000);

        var gpItem = Rs2Bank.bankItems.stream().filter(x -> x.id == ItemID.COINS
                        || x.id == ItemID.COINS_995 || x.id == ItemID.COINS_6964 || x.id == ItemID.COINS_8890).findAny().orElse(null);
        var gp = gpItem == null ? 0 : gpItem.quantity;

        for (var item : items){
            if (Rs2Inventory.contains(item.getId()))
                return true;
            else if (Rs2Bank.hasItem(item.getId())){
                Rs2Bank.withdrawItem(item.getId());
                return true;
            }
            else if (Microbot.getVarbitPlayerValue(VarPlayer.QUEST_POINTS) > 10 && Microbot.getClient().getTotalLevel() > 100
                    && (item.getGp() < gp || Rs2GrandExchange.isAtGrandExchange() && Rs2GrandExchange.getOffer(item.getId()) != null)){
                var offer = Rs2GrandExchange.getOffer(item.getId());
                if (offer == null){
                    if (Rs2GrandExchange.isAtGrandExchange()){
                        Rs2GrandExchange.openExchange();
                        Rs2GrandExchange.collectToBank();
                    }

                    if (!Rs2GrandExchange.buyItem(item.getName(), item.getGp(), 1))
                        return false;
                }

                return offer != null && offer.getState() == GrandExchangeOfferState.BOUGHT && Rs2GrandExchange.collectToInventory();
            }
        }

        return false;
    }
}
