package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

import java.awt.*;
import java.util.Comparator;

public class NatureSpiritTask extends AccountBuilderQuestTask {
    public NatureSpiritTask(){
        super(QuestHelperQuest.NATURE_SPIRIT,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemCollections.COMBAT_BRACELETS, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true));
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45;
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Widget.hasWidget("Enter the swamp."))
            Rs2Widget.clickWidget("Enter the swamp.");
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Cast the druidic spell next to a rotten log in Mort Myre"))){
            if (!Rs2Walker.walkTo(new WorldPoint(3423, 3423, 0)))
                return;

            var fungi = Rs2GameObject.getGameObjects(ObjectID.FUNGI_ON_LOG)
                    .stream().sorted(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation())))
                    .findAny().orElse(null);

            if (fungi != null){
                Rs2GameObject.interact(fungi, "Pick");
                Rs2Player.waitForAnimation();
            } else {
                var logs = Rs2GameObject.getGameObjects(ObjectID.ROTTING_LOG)
                        .stream().min(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()))).orElse(null);

                if (logs == null)
                    return;

                if (Rs2Player.getWorldLocation().distanceTo(logs.getWorldLocation()) > 1){
                    Rs2Walker.walkFastCanvas(logs.getWorldLocation());
                    Rs2Player.waitForWalking();
                } else {
                    Microbot.doInvoke(new NewMenuEntry(4, 9764864, MenuAction.CC_OP.getId(), 2, 2968, "Druidic spell"), new Rectangle(0, 0, 1, 1));
                    sleep(5000, 6000);
                }
            }
        } else if (step.getText().stream().anyMatch(x -> x.contains("Stand on the orange stone outside the grotto."))){
            Rs2Walker.walkFastCanvas(step.getWorldPoint());
            Rs2Player.waitForWalking();
        } else if (step.getText().stream().anyMatch(x -> x.contains("Right-click 'bloom' the blessed sickle next to rotten logs"))){
            if (!Rs2Walker.walkTo(new WorldPoint(3423, 3423, 0)))
                return;

            if (Rs2Inventory.count(ItemID.MORT_MYRE_FUNGUS) >= 3){
                Rs2Inventory.interact(ItemID.DRUID_POUCH, "Fill");
                return;
            }

            var fungi = Rs2GameObject.getGameObjects(ObjectID.FUNGI_ON_LOG)
                    .stream().filter(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 2)
                    .findAny().orElse(null);

            if (fungi != null){
                Rs2GameObject.interact(fungi, "Pick");
                Rs2Player.waitForAnimation();
            } else {
                var logs = Rs2GameObject.getGameObjects(ObjectID.ROTTING_LOG)
                        .stream().min(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()))).orElse(null);

                if (logs == null)
                    return;

                if (Rs2Player.getWorldLocation().distanceTo(logs.getWorldLocation()) > 1){
                    var tile = Rs2Tile.getWalkableTilesAroundTile(logs.getWorldLocation(), 1).stream().filter(x -> !x.equals(logs.getWorldLocation()));
                    Rs2Walker.walkFastCanvas(tile.findFirst().orElse(null));
                    Rs2Player.waitForWalking();
                } else {
                    Rs2Inventory.interact(ItemID.SILVER_SICKLE_B, "Cast Bloom");
                    Rs2Player.waitForAnimation();
                }
            }
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.GHAST){
            Rs2Inventory.useItemOnNpc(ItemID.DRUID_POUCH_2958, NpcID.GHAST);
            Rs2Player.waitForAnimation();
        }
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
