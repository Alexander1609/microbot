package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.wintertodt.MWintertodtConfig;
import net.runelite.client.plugins.microbot.wintertodt.MWintertodtScript;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class WintertodtTask extends AccountBuilderTask {
    MWintertodtScript wintertodtScript = new MWintertodtScript();

    @Override
    public String getName() {
        return "Firemaking: Wintertodt";
    }

    public WintertodtTask(){
        skill = Skill.FIREMAKING;
        minLevel = 50;
        maxLevel = 99;

        addRequirement(ItemID.KNIFE, 1);
        addRequirement(ItemID.RUNE_AXE, 1);
        addRequirement(ItemCollections.GAMES_NECKLACES, 1);
        addRequirement(ItemID.SWORDFISH, 100);
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING) >= 41
                && isQuestCompleted(QuestHelperQuest.CLIENT_OF_KOUREND);
    }

    @Override
    public void run() {
        super.run();

        blockStuckPrevention = true;
        wintertodtScript.run(new MWintertodtConfig() {
            @Override
            public boolean axeInInventory() {
                return true;
            }

            @Override
            public Rs2Food food() {
                return Rs2Food.SWORDFISH;
            }

            @Override
            public int foodAmount() {
                return 8;
            }

            @Override
            public int eatAt() {
                return 40;
            }
        });
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        var inRoom = Rs2Player.getWorldLocation().distanceTo(new WorldArea(1615, 3931, 34, 33, 0)) != 0;
        while (inRoom){
            if (Global.sleepUntilTrue(() -> Rs2Player.getWorldLocation().distanceTo(new WorldArea(1615, 3931, 34, 33, 0)) == 0, 10, 1000))
                inRoom = false;
        }

        super.doTaskCleanup(shutdown);

        wintertodtScript.shutdown();
        blockStuckPrevention = false;
    }

    @Override
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        wintertodtScript.onHitsplatApplied(hitsplatApplied);
    }

    boolean equippedWarmClothing = false;
    List<Integer> warmClothings = List.of(
            ItemID.PYROMANCER_ROBE,
            ItemID.PYROMANCER_BOOTS,
            ItemID.PYROMANCER_GARB,
            ItemID.PYROMANCER_HOOD,
            ItemID.BRUMA_TORCH,
            ItemID.TOME_OF_FIRE_EMPTY,
            ItemID.WARM_GLOVES);

    List<Integer> wintertodtWorlds = List.of(307, 309, 311, 389);

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        if (!equippedWarmClothing && !Rs2Bank.walkToBankAndUseBank())
            return false;

        for(var item : warmClothings){
            if (!Rs2Equipment.isWearing(item) && Rs2Bank.hasBankItem(item, 1)){
                Rs2Bank.withdrawAndEquip(item);
                sleep(400, 700);
            }
        }
        equippedWarmClothing = true;

        if (!Rs2Walker.walkTo(new WorldPoint(1636, 3944, 0)))
            return false;

        if (Rs2Bank.isOpen()){
            Rs2Bank.closeBank();
            return false;
        }

        if (!wintertodtWorlds.contains(Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWorld()))) {
            var worlds = Microbot.getWorldService().getWorlds();
            var bestWorld = wintertodtWorlds.stream().map(x -> worlds.findWorld(x)).max(Comparator.comparing(World::getPlayers)).orElse(null);

            Microbot.getClient().openWorldHopper();
            sleepUntil(() -> !Rs2Widget.isHidden(ComponentID.WORLD_SWITCHER_WORLD_LIST), 2000);
            sleep(500);

            Microbot.doInvoke(new NewMenuEntry(bestWorld.getId(), 4522002, MenuAction.CC_OP.getId(), 1, -1, ""), new Rectangle(1, 1));
            sleep(5000);
        }

        return true;
    }

    @Override
    public void tick() {
        super.tick();

        for(var item : warmClothings){
            if (!Rs2Equipment.isWearing(item) && Rs2Inventory.contains(item)){
                Rs2Inventory.wear(item);
                sleep(200);
            }
        }
    }

    @Override
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN){
            if (!wintertodtWorlds.contains(Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWorld()))) {
                var worlds = Microbot.getWorldService().getWorlds();
                var bestWorld = wintertodtWorlds.stream().map(x -> worlds.findWorld(x)).max(Comparator.comparing(World::getPlayers)).orElse(null);

                Microbot.getClient().openWorldHopper();
                sleepUntil(() -> !Rs2Widget.isHidden(ComponentID.WORLD_SWITCHER_WORLD_LIST), 2000);
                sleep(500);

                Microbot.doInvoke(new NewMenuEntry(bestWorld.getId(), 4522002, MenuAction.CC_OP.getId(), 1, -1, ""), new Rectangle(1, 1));
                sleep(5000);
            }
        }
    }
}
