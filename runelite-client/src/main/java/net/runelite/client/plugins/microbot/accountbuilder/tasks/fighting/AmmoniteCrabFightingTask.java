package net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript;
import net.runelite.client.plugins.microbot.playerassist.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;

public class AmmoniteCrabFightingTask extends AccountBuilderFightingTask {
    public AmmoniteCrabFightingTask(){
        super(new PlayerAssistConfig() {
            @Override
            public boolean toggleCombat() {
                return true;
            }

            @Override
            public String attackableNpcs() {
                return "Ammonite Crab";
            }

            @Override
            public boolean toggleFood() {
                return true;
            }

            @Override
            public WorldPoint centerLocation() {
                return new WorldPoint(3723, 3893, 0);
            }

            @Override
            public int attackRadius() {
                return 20;
            }

            @Override
            public boolean toggleOnlyLootMyItems() {
                return true;
            }

            @Override
            public boolean toggleBalanceCombatSkills() {
                return true;
            }

            @Override
            public PlayStyle playStyle() {
                return PlayStyle.PASSIVE;
            }
        });

        addRequirement(ItemCollections.AMULET_OF_GLORIES, true);
        addRequirement(ItemCollections.ARDY_CLOAKS, true);
        addRequirement(ItemCollections.COMBAT_BRACELETS, true);
        addRequirement(ItemID.WARRIOR_RING, true);
        addRequirement(ItemID.RUNE_BOOTS, true);
        addRequirement(ItemID.RUNE_PLATELEGS, true);
        addRequirement(ItemID.RUNE_KITESHIELD, true);
        addRequirement(ItemID.RUNE_CHAINBODY, true);
        addRequirement(ItemID.RUNE_FULL_HELM, true);
        addRequirement(ItemID.RUNE_SCIMITAR, true);
        addRequirement(ItemID.SWORDFISH, 200);
        addRequirement(ItemID.SWORDFISH, 20);
    }

    String food = "Swordfish";
    WorldArea crabArea = new WorldArea(3719, 3890, 9, 8, 0);
    long lastCombatTime = 0;
    int clickCount = 0;

    @Override
    public String getName() {
        return "Fighting: Ammonite crabs";
    }

    @Override
    public boolean requirementsMet() {
        return isQuestCompleted(QuestHelperQuest.BONE_VOYAGE)
                && Microbot.getClient().getRealSkillLevel(Skill.ATTACK) >= 40
                && Microbot.getClient().getRealSkillLevel(Skill.STRENGTH) >= 40
                && Microbot.getClient().getRealSkillLevel(Skill.DEFENCE) >= 40
                && (Microbot.getClient().getRealSkillLevel(Skill.ATTACK) < 70
                || Microbot.getClient().getRealSkillLevel(Skill.STRENGTH) < 70
                || Microbot.getClient().getRealSkillLevel(Skill.DEFENCE) < 70);
    }

    @Override
    public void tick() {
        if (Rs2Combat.inCombat())
            lastCombatTime = System.currentTimeMillis();

        if (!Rs2Inventory.hasItem(food)) {
            attackNpc.shutdown();
            getFoodFromBank();
        } else if (clickCount > 5){
            attackNpc.shutdown();
            if (Rs2Walker.walkTo(new WorldPoint(3699, 3900, 0)))
                clickCount = 0;
        } else if (Rs2Player.getWorldLocation().distanceTo(crabArea) < 10 && Rs2Player.getPlayers().stream().anyMatch(x -> x.isInteracting() && x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 10)) {
            attackNpc.shutdown();

            if (lastCombatTime + 10_000 < System.currentTimeMillis())
                Rs2Player.logout();
        } else if (!attackNpc.isRunning())
            attackNpc.run(config);

        if (Rs2Player.isAnimating() || Rs2Player.isMoving() || Microbot.pauseAllScripts) return;

        if (Rs2Inventory.hasItem(food) && !Rs2Combat.inCombat() && Rs2Player.getWorldLocation().distanceTo(crabArea) > 10) {
            Rs2Walker.walkTo(crabArea, 10);
        } else if (attackNpc.isRunning() && AttackNpcScript.attackableNpcs.isEmpty()){
            var rock = Rs2Npc.getNpc(NpcID.FOSSIL_ROCK);
            clickCount++;
            Rs2Walker.walkFastCanvas(rock.getWorldLocation());
        } else {
            clickCount = 0;
        }
    }

    private boolean getFoodFromBank(){
        if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
            return false;

        Rs2Bank.depositAll();

        if (!Rs2Bank.hasBankItem(food, 20)){
            cancel();
            return false;
        } else {
            Rs2Bank.withdrawX(food, 20);
            return true;
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        return Rs2Walker.walkTo(crabArea, 1);
    }
}
