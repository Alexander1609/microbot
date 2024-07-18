package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.enums.Birdhouse;
import net.runelite.client.plugins.microbot.accountbuilder.enums.BirdhouseLocation;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseState;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseTracker;

import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BirdhouseTask extends AccountBuilderTask {
    @Override
    public String getName() {
        return "Hunter: Birdhouse run";
    }

    public BirdhouseTask(){
        skill = Skill.HUNTER;
        minLevel = 5;
    }

    Birdhouse birdhouse = null;

    @Override
    public void run() {
        birdhouse = getHouseType();

        super.run();
    }

    @Override
    public void tick() {
        var data = BirdHouseTracker.getBirdHouseData().values();

        for (var location : BirdhouseLocation.values()){
            if (BirdHouseState.fromVarpValue(Microbot.getVarbitPlayerValue(location.getSpace().getVarp())) == BirdHouseState.SEEDED
                && data.stream().filter(x -> x.getSpace() == location.getSpace()).findFirst().get().getTimestamp() + 60*50 > Instant.now().getEpochSecond())
                continue;

            if (Rs2Player.getWorldLocation().distanceTo(location.getWorldPoint()) > 20){
                switch (location){
                    case MEADOW_NORTH:
                        while (!Global.sleepUntilTrue(() -> Rs2Widget.isWidgetVisible(608, 0), 10, 1000)){
                            Rs2GameObject.interact("Magic Mushtree");
                            Rs2Player.waitForWalking();
                            sleep(500);
                        }

                        Rs2Widget.clickWidget(39845903);
                        sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(location.getWorldPoint()) <= 20, 2000);
                        break;
                    case MEADOW_SOUTH:
                        Rs2Walker.walkTo(location.getWorldPoint());
                        break;
                    case VALLEY_NORTH:
                        if (!Rs2Walker.walkTo(new WorldPoint(3677, 3870, 0)))
                            return;

                        while (!Global.sleepUntilTrue(() -> Rs2Widget.isWidgetVisible(608, 0), 10, 1000)){
                            Rs2GameObject.interact("Magic Mushtree");
                            Rs2Player.waitForWalking();
                            sleep(500);
                        }

                        Rs2Widget.clickWidget(39845895);
                        sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(location.getWorldPoint()) <= 20, 2000);
                        break;
                }

                return;
            } else {
                var state = BirdHouseState.fromVarpValue(Microbot.getVarbitPlayerValue(location.getSpace().getVarp()));

                switch (state){
                    case SEEDED:
                        Rs2GameObject.interact(location.getObjectId(), "empty");
                        Rs2Player.waitForAnimation();
                        break;
                    case EMPTY:
                        if (Rs2Inventory.hasItem("house"))
                            Rs2GameObject.interact(location.getWorldPoint(), "build");
                        else if (Rs2Inventory.contains(ItemID.CLOCKWORK)) {
                            Rs2Inventory.combine(ItemID.HAMMER, birdhouse.getLogsId());
                            if (Rs2Inventory.count(ItemID.CLOCKWORK) > 1){
                                sleep(500, 1000);
                                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                            }
                        }

                        Rs2Player.waitForAnimation();
                        break;
                    case BUILT:
                        Rs2Inventory.use(ItemID.POTATO_SEED);
                        sleep(500, 1000);
                        Rs2GameObject.interact(location.getWorldPoint());
                        Rs2Player.waitForAnimation();
                        break;
                }

                return;
            }
        }

        cancel();
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        if (ItemCollections.DIGSITE_PENDANTS.getItems().stream().noneMatch(Rs2Inventory::contains)
                && Rs2Player.getWorldLocation().distanceTo(new WorldPoint(3764, 3878, 1)) > 20){
            if (Rs2Bank.isOpen()){
                Rs2Bank.closeBank();
                return false;
            } else {
                Rs2Magic.cast(MagicAction.ENCHANT_RUBY_JEWELLERY);
                sleep(500, 1000);
                Rs2Inventory.interact(ItemID.RUBY_NECKLACE);
            }
        }

        return Rs2Walker.walkTo(new WorldPoint(3764, 3878, 1));
    }

    @Override
    public boolean requirementsMet() {
        updateItemRequirements();

        return super.requirementsMet()
                && isQuestCompleted(QuestHelperQuest.BONE_VOYAGE)
                && Microbot.getClient().getRealSkillLevel(Skill.CRAFTING) >= 5
                && BirdHouseTracker.getBirdHouseData().values()
                    .stream().allMatch(x -> BirdHouseState.fromVarpValue(x.getVarp()) == BirdHouseState.EMPTY
                        || x.getTimestamp() + 60*50 < Instant.now().getEpochSecond());
    }

    private void updateItemRequirements() {
        itemRequirements = new ArrayList<>();
        addRequirement(ItemCollections.ARDY_CLOAKS, true);
        addRequirement(ItemID.HAMMER, 1);
        addRequirement(ItemID.CHISEL, 1);
        addRequirement(ItemID.POTATO_SEED, 40, 600);

        var missingHouses = (int)BirdHouseTracker.getBirdHouseData().values().stream().filter(x -> BirdHouseState.fromVarpValue(x.getVarp()) == BirdHouseState.EMPTY).count();
        if (missingHouses > 0)
            addRequirement(ItemID.CLOCKWORK, missingHouses);

        addRequirement(getHouseType().getLogsId(), 4, 100);

        if (Rs2Player.getWorldLocation().distanceTo(new WorldPoint(3764, 3878, 1)) > 20){
            if (ItemCollections.DIGSITE_PENDANTS.getItems().stream().anyMatch(x -> Rs2Bank.hasBankItem(x, 1)))
                addRequirement(ItemCollections.DIGSITE_PENDANTS, 1);
            else {
                addRequirement(ItemID.RUBY_NECKLACE, 1, 10);
                addRequirement(ItemID.COSMIC_RUNE, 1, 100);
                addRequirement(ItemID.FIRE_RUNE, 5, 500);
            }
        }
    }

    private Birdhouse getHouseType(){
        return Arrays.stream(Birdhouse.values())
                .filter(x -> Microbot.getClient().getRealSkillLevel(Skill.HUNTER) >= x.getHunterLevel()
                    && Microbot.getClient().getRealSkillLevel(Skill.CRAFTING) >= x.getCraftingLevel())
                .max(Comparator.comparing(Birdhouse::getHunterLevel))
                .orElse(null);
    }
}
