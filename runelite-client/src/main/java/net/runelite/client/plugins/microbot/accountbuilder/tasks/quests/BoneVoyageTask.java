package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

public class BoneVoyageTask extends AccountBuilderQuestTask {
    public BoneVoyageTask(){
        super(QuestHelperQuest.BONE_VOYAGE,
                new ItemRequirement("Comsmic", ItemID.COSMIC_RUNE, 1),
                new ItemRequirement("Fire", ItemID.FIRE_RUNE, 5),
                new ItemRequirement("Ruby neck", ItemID.RUBY_NECKLACE, 1),
                new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true));
    }

    int currentDir = 0;

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.SAWMILL_OPERATOR){
            if (Rs2Player.getWorldLocation().distanceTo(step.getWorldPoint()) < 5 && !Rs2Dialogue.isInDialogue()) {
                Rs2Npc.interact(step.npcID);
                Rs2Player.waitForWalking();
            }
        } else if (step.npcID == NpcID.LEAD_NAVIGATOR){
            if (Rs2Widget.isWidgetVisible(604, 4)){
                stopQuest();
                blockStuckPrevention = true;

                if (!Rs2Widget.isWidgetVisible(604, 15))
                    Rs2Widget.clickWidget(604, 11);

                var direction =  Microbot.getClient().getVarcIntValue(197);
                if (direction > 36_000 && currentDir != 1){
                    Rs2Widget.clickWidget(604, 38);
                    sleep(200, 500);
                    Rs2Widget.clickWidget(604, 38);
                    currentDir = 1;
                } else if (direction < 31_000 && currentDir != -1){
                    Rs2Widget.clickWidget(604, 39);
                    sleep(200, 500);
                    Rs2Widget.clickWidget(604, 39);
                    currentDir = -1;
                }
            } else {
                startupQuest();
            }
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (!super.doTaskPreparations())
            return false;

        if (Rs2Bank.isOpen()){
            Rs2Bank.closeBank();
            return false;
        }

        Rs2Magic.cast(MagicAction.ENCHANT_RUBY_JEWELLERY);
        sleep(500, 1000);
        Rs2Inventory.interact(ItemID.RUBY_NECKLACE);
        Rs2Inventory.waitForInventoryChanges();
        return true;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        while (!Rs2Walker.walkTo(new WorldPoint(3764, 3873, 1)))
            sleep(500);

        while (!Global.sleepUntilTrue(() -> Rs2Widget.isWidgetVisible(608, 0), 10, 1000)){
            Rs2GameObject.interact("Magic Mushtree");
            Rs2Player.waitForWalking();
            sleep(500);
        }

        while (!Rs2Walker.walkTo(new WorldPoint(3764, 3873, 1)))
            sleep(500);

        while (!Global.sleepUntilTrue(() -> Rs2Dialogue.isInDialogue() && Rs2Widget.getWidget(ComponentID.DIALOG_SPRITE_TEXT).getText().contains("It seems to bind its magic"), 10, 2000)){
            Rs2Inventory.interact(x -> ItemCollections.DIGSITE_PENDANTS.getItems().contains(x.id));
            sleep(200, 500);
            Rs2GameObject.interact(30943);
            sleepUntil(Rs2Dialogue::isInDialogue, 5000);
        }

        while (!Rs2Walker.walkTo(new WorldPoint(3678, 3869, 0)))
            sleep(500);

        while (!Global.sleepUntilTrue(() -> Rs2Widget.isWidgetVisible(608, 0), 10, 1000)){
            Rs2GameObject.interact("Magic Mushtree");
            Rs2Player.waitForWalking();
            sleep(500);
        }

        while (!Rs2Walker.walkTo(new WorldPoint(3759, 3756, 0)))
            sleep(500);

        while (!Global.sleepUntilTrue(() -> Rs2Widget.isWidgetVisible(608, 0), 10, 1000)){
            Rs2GameObject.interact("Magic Mushtree");
            Rs2Player.waitForWalking();
            sleep(500);
        }

        super.doTaskCleanup(shutdown);
    }
}
