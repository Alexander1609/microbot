package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.Restriction;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.Arrays;
import java.util.Comparator;

public class ArdougneEasyDiaryTask extends AccountBuilderQuestTask {
    public ArdougneEasyDiaryTask(){
        super(QuestHelperQuest.ARDOUGNE_EASY);
    }

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.BAKERS_STALL_11730){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                return;

            if (Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2669, 3310, 0)) != 0){
                Rs2Walker.walkFastCanvas(new WorldPoint(2669, 3310, 0));
                return;
            }

            startupQuest();
        } else if (step.objectID == ObjectID.GANGPLANK_4977){
            if (isQuestRunning() && Rs2Player.getWorldLocation().distanceTo(new WorldPoint(2672, 3169, 1)) < 5){
                stopQuest();
                sleep(65_000);
            }

            Rs2Dialogue.clickContinue();

            if (!isQuestRunning())
                startupQuest();
        } else if (step.objectID == ObjectID.ALTAR){
            if (Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) == Microbot.getClient().getRealSkillLevel(Skill.PRAYER)){
                if (isQuestRunning())
                    stopQuest();

                Rs2Prayer.toggle(Rs2PrayerEnum.THICK_SKIN, true);
            } else if (!isQuestRunning()){
                Rs2Prayer.toggle(Rs2PrayerEnum.THICK_SKIN, false);
                startupQuest();
            }
        } else if (step.objectID == ObjectID.GATE_2041){
            if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3148, 3917, 16, 14, 0)) == 0){
                Rs2GameObject.interact(1815, "Pull");
                Rs2Player.waitForAnimation();
            }
        }
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.TINDEL_MARCHANT){
            Rs2Dialogue.clickContinue();

            if (Rs2GameObject.findObjectById(2476) != null){
                Rs2Walker.setTarget(null);
                Rs2GameObject.interact(2476, "Climb-on");
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(2876, 4802, 62, 62, 0)) == 0
                && Rs2Npc.getNpc("Portal", false) != null){
            Rs2Walker.setTarget(null);
            var npc = Rs2Npc.getNpc("Portal", false);
            Rs2Npc.interact(npc, "Use");
            Rs2Player.waitForWalking();
            startupQuest();
        }
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getRealSkillLevel(Skill.THIEVING) >= 15
                && Microbot.getClient().getRealSkillLevel(Skill.AGILITY) >= 30;
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory())
            return false;

        ShortestPathPlugin.getPathfinderConfig().setRestrictedTiles(
                new Restriction(3118, 3243, 0),
                new Restriction(3119, 3244, 0),
                new Restriction(3118, 3245, 0),
                new Restriction(3117, 3244, 0)
        );

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3183, 9603, 8, 14, 0)) == 0){
            Rs2Walker.setTarget(null);
            Rs2GameObject.interact(5501, "Pick-lock");
            Rs2Player.waitForAnimation();
            sleep(1000, 1500);
            return false;
        }

        if (Rs2Inventory.count(ItemID.RUSTY_SWORD) + Rs2Bank.count(ItemID.RUSTY_SWORD) < 2){
            if (!Rs2Inventory.contains(ItemID.SHRIMPS)){
                if (!Rs2Bank.walkToBankAndUseBank())
                    return false;

                Rs2Bank.withdrawX(ItemID.SHRIMPS, 20);
                return false;
            }

            if (!Rs2Walker.walkTo(new WorldPoint(3166, 9633, 0), 10))
                return false;

            if ((double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) < 50){
                Rs2Inventory.interact(ItemID.SHRIMPS, "Eat");
                return false;
            }

            if (Rs2Inventory.getEmptySlots() <= 1){
                if (Rs2Inventory.contains("Coin pouch")){
                    Rs2Inventory.interact("Coin pouch", "Open-all");
                    sleep(400, 700);
                }

                Rs2Inventory.dropAllExcept(ItemID.SHRIMPS, ItemID.RUSTY_SWORD, ItemID.COINS, ItemID.COINS_995, ItemID.COINS_6964, ItemID.COINS_8890);
                return false;
            }

            if (!Rs2Player.isAnimating() && !Rs2Player.isMoving()){
                var npc = Rs2Npc.getNpcs("H.A.M. Member")
                        .filter(x -> Arrays.asList(Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getNpcDefinition(x.getId())).getActions()).contains("Pickpocket")
                                && Rs2Npc.canWalkTo(x, 10))
                        .min(Comparator.comparing(x -> Rs2Player.getWorldLocation().distanceTo(x.getWorldLocation()))).orElse(null);

                Rs2Npc.interact(npc, "Pickpocket");
                Rs2Player.waitForAnimation();
                return false;
            }

            return false;
        }

        if (Rs2Inventory.contains("Coin pouch")){
            Rs2Inventory.interact("Coin pouch", "Open-all");
            sleep(400, 700);
        }

        if (Rs2Inventory.contains(ItemID.SHRIMPS)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        return withdrawBuyItems();
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        Rs2Tab.switchToInventoryTab();
        sleep(500, 1000);
        Rs2Inventory.interact("Antique lamp", "Rub");
        sleepUntil(() -> Rs2Widget.isWidgetVisible(240, 0), 5000);
        Rs2Widget.clickWidget(240, 9); // Agility
        sleep(500, 1000);
        Rs2Widget.clickWidget(240, 26);
    }
}
