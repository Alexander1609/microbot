package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.shortestpath.WorldPointUtil;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

public class TreeGnomeVillageTask extends AccountBuilderQuestTask {
    NPC warlord;
    boolean hasAdventurerGear = true;
    boolean gettingAdventurerGear = false;

    public TreeGnomeVillageTask(){
        super(QuestHelperQuest.TREE_GNOME_VILLAGE,
                new ItemRequirement("Swordfish", ItemID.SWORDFISH, 15));
        useFood = true;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet() && Microbot.getClient().getLocalPlayer().getCombatLevel() >= 35 && Microbot.getClient().getRealSkillLevel(Skill.MAGIC) >= 10;
    }

    @Override
    protected void handleDetailedStep(DetailedQuestStep step) {
        if (step.getText().stream().anyMatch(x -> x.contains("Follow the marked path to walk through the maze.")))
            Rs2Walker.walkTo(new WorldPoint(2541, 3170, 0));
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.TRACKER_GNOME_2){
            if (!Rs2Walker.walkTo(new WorldPoint(2523, 3256, 0))){
                if (isQuestRunning())
                    stopQuest();

                Rs2Walker.setTarget(null);
                Rs2Npc.interact(NpcID.TRACKER_GNOME_2);
                sleepUntil(Rs2Dialogue::isInDialogue, 5000);
            } else if (!isQuestRunning())
                startupQuest();
        } else if (step.npcID == NpcID.KHAZARD_WARLORD_7622){
            if (isQuestRunning())
                stopQuest();

            if (warlord == null)
                warlord = Rs2Npc.getNpc(NpcID.KHAZARD_WARLORD_7622);

            if (warlord.isDead() && !isQuestRunning() || warlord.getCanvasTilePoly() == null){
                startupQuest();
                warlord = null;
                return;
            }

            var aggroSpot = new WorldPoint(2446, 3302, 0);
            var safeSpot = new WorldPoint(2444, 3297, 0);

            if (warlord.getWorldLocation().distanceTo(aggroSpot) > 3)
                Rs2Walker.walkFastCanvas(aggroSpot);
            else if (Rs2Player.getWorldLocation().distanceTo(safeSpot) > 0)
                Rs2Walker.walkFastCanvas(safeSpot);
            else if (!Rs2Combat.inCombat())
                Rs2Npc.interact(warlord, "Attack");
        }
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.BALLISTA && !Rs2Dialogue.isInDialogue())
            Rs2GameObject.interact(ObjectID.BALLISTA, "Fire");
        else if (step.objectID == ObjectID.LADDER_16683 && !WorldPointUtil.isPointInPolygon(Rs2Player.getWorldLocation(), new WorldPoint[]{
                new WorldPoint(2500, 3256, 0),
                new WorldPoint(2504, 3256, 0),
                new WorldPoint(2505, 3255, 0),
                new WorldPoint(2506, 3255, 0),
                new WorldPoint(2506, 3254, 0),
                new WorldPoint(2503, 3251, 0),
                new WorldPoint(2501, 3251, 0),
                new WorldPoint(2500, 3252, 0)
            })){
            if (isQuestRunning())
                stopQuest();

            if (!WorldPointUtil.isPointInPolygon(Rs2Player.getWorldLocation(), new WorldPoint[]{
                    new WorldPoint(2511, 3254, 0),
                    new WorldPoint(2512, 3255, 0),
                    new WorldPoint(2512, 3258, 0),
                    new WorldPoint(2511, 3259, 0),
                    new WorldPoint(2501, 3259, 0),
                    new WorldPoint(2500, 3258, 0),
                    new WorldPoint(2500, 3257, 0),
                    new WorldPoint(2504, 3257, 0),
                    new WorldPoint(2505, 3256, 0),
                    new WorldPoint(2506, 3256, 0),
                    new WorldPoint(2507, 3254, 0)
            })){
                if (Rs2Dialogue.isInDialogue()){
                    Rs2Dialogue.clickContinue();
                    Rs2Player.waitForAnimation();
                    return;
                }

                if (!Rs2Walker.walkTo(new WorldPoint(2509, 3253, 0)))
                    return;

                Rs2GameObject.interact(new WorldPoint(2509, 3253, 0));
            } else {
                Rs2GameObject.interact(new WorldPoint(2505, 3256, 0), "Open");
                Rs2Player.waitForWalking();
                Rs2Walker.walkFastCanvas(new WorldPoint(2503, 3254, 0));
                Rs2Player.waitForWalking();
            }
        } else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Equipment.isWearing(ItemID.STAFF_OF_EARTH)){
            if (hasAdventurerGear){
                if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                    return false;

                Rs2Bank.depositEquipment();

                if (!Rs2Bank.hasBankItem(ItemID.STAFF_OF_EARTH, 1))
                    hasAdventurerGear = false;
                else {
                    Rs2Bank.withdrawAndEquip(ItemID.STAFF_OF_EARTH);
                    Rs2Bank.withdrawAndEquip(ItemID.AMULET_OF_ACCURACY);
                }
            } else {
                if (Rs2Inventory.getEmptySlots() < 28 && !gettingAdventurerGear){
                    if (!Rs2Bank.walkToBankAndUseBank())
                        return false;

                    Rs2Bank.depositAll();
                    return false;
                } else
                    gettingAdventurerGear = true;

                if (!Rs2Inventory.hasItem(ItemID.STAFF_OF_EARTH) || Rs2Dialogue.isInDialogue()){
                    if (!Rs2Walker.walkTo(new WorldPoint(3232, 3233, 0), 2))
                        return false;

                    if (!Rs2Dialogue.isInDialogue()) {
                        Rs2Npc.interact(NpcID.ADVENTURER_JON_9244, "Claim");
                        sleepUntil(Rs2Dialogue::isInDialogue, 5_000);
                    }
                    else if (Rs2Dialogue.hasSelectAnOption())
                        Rs2Walker.walkFastCanvas(Rs2Player.getWorldLocation());
                    else
                        Rs2Dialogue.clickContinue();
                } else {
                    if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                        return false;

                    Rs2Bank.depositAll();
                    hasAdventurerGear = true;
                }
            }

            return false;
        }

        if (!Rs2Inventory.contains(ItemID.BRONZE_AXE)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            Rs2Bank.withdrawItem(ItemID.BRONZE_AXE);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.MIND_RUNE, 200)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawX(ItemID.MIND_RUNE, 200);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.AIR_RUNE, 200)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawX(ItemID.AIR_RUNE, 200);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount("Coins", 50, true)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.withdrawX("Coins", 100);
            return false;
        }

        if (!Rs2Inventory.hasItemAmount(ItemID.LOGS, 6)){
            if (!Rs2Walker.walkTo(new WorldPoint(3189, 3246, 0), 15))
                return false;

            var trees = Rs2GameObject.findClosestObjects("Tree", true, 20, Rs2Player.getWorldLocation());

            if (trees.isEmpty())
                return false;

            Rs2GameObject.interact(trees.get(0), "Chop down");
            Rs2Player.waitForAnimation();

            return false;
        }

        if (Rs2Tab.getCurrentTab() != InterfaceTab.COMBAT) {
            Rs2Tab.switchToCombatOptionsTab();
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.COMBAT, 2000);
        }

        Rs2Widget.clickWidget(WidgetInfo.COMBAT_SPELL_BOX);
        sleepUntil(() -> Rs2Widget.isWidgetVisible(201, 1), 5000);
        var childs = Rs2Widget.getWidget(201, 1).getDynamicChildren();
        Rs2Widget.clickWidgetFast(childs[3], 3);

        return withdrawBuyItems();
    }
}