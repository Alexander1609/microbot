package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

public class KandarinEasyDiaryTask extends AccountBuilderQuestTask {
    public KandarinEasyDiaryTask(){
        super(QuestHelperQuest.KANDARIN_EASY,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemCollections.COMBAT_BRACELETS, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true));
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == NullObjectID.NULL_8176){
            stopQuest();

            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                return;

            Rs2GameObject.interact(NullObjectID.NULL_8176, "Rake");
            Rs2Player.waitForAnimation();
            if (!sleepUntilTrue(Rs2Player::isAnimating, 100, 5000)) return;
            sleepUntil(() -> !Rs2Player.isAnimating(), 20_000);
            Rs2Inventory.useItemOnObject(ItemID.JUTE_SEED, NullObjectID.NULL_8176);
        }
    }

    Map<Integer, WorldPoint> elementals = Map.of(
            NpcID.FIRE_ELEMENTAL, new WorldPoint(2719, 9877, 0),
            NpcID.WATER_ELEMENTAL, new WorldPoint(2719, 9903, 0),
            NpcID.AIR_ELEMENTAL, new WorldPoint(2735, 9891, 0),
            NpcID.EARTH_ELEMENTAL, new WorldPoint(2700, 9903, 0));

    ArrayList<Integer> killedElementals = new ArrayList<>();
    NPC target = null;

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.CANDLE_MAKER){
            if (Rs2Shop.isOpen()) {
                stopQuest();
                Rs2Shop.buyItem("Candle", "1");
            }
            else
                startupQuest();
        } else if (step.getText().stream().anyMatch(x -> x.contains("Kill one of each of the 4 elementals."))){
            stopQuest();

            var nextElemental = elementals.entrySet().stream().filter(x -> !killedElementals.contains(x.getKey()))
                    .sorted(Comparator.comparing(x -> x.getValue().distanceTo(Rs2Player.getWorldLocation())))
                    .findFirst().orElse(null);
            if (nextElemental == null) return;

            if (target == null){
                if (!Rs2Walker.walkTo(nextElemental.getValue())) return;

                var npc = Rs2Npc.getNpcs(nextElemental.getKey()).filter(x -> !x.isInteracting() && !x.isDead())
                        .sorted(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation())))
                        .findFirst().orElse(null);
                if (npc == null) return;

                target = npc;
            } else if (target.isDead()){
                killedElementals.add(target.getId());
                target = null;
            } else if (!Rs2Combat.inCombat())
                Rs2Npc.attack(target);
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45
                && Microbot.getClient().getRealSkillLevel(Skill.SMITHING) >= 30;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        Rs2Tab.switchToInventoryTab();
        sleep(500, 1000);
        Rs2Inventory.interact("Antique lamp", "Rub");
        sleepUntil(() -> Rs2Widget.isWidgetVisible(240, 0), 5000);
        Rs2Widget.clickWidget(240, 14); // Slayer
        sleep(500, 1000);
        Rs2Widget.clickWidget(240, 26);
    }
}
