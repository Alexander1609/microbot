package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GrandTreeTask extends AccountBuilderQuestTask {
    boolean talkedWithForeman = false;

    public GrandTreeTask(){
        super(QuestHelperQuest.THE_GRAND_TREE);
        skill = Skill.MAGIC;
        minLevel = 13;
    }

    @Override
    public boolean requirementsMet() {
        return super.requirementsMet()
                && Microbot.getClient().getLocalPlayer().getCombatLevel() > 45;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (step.npcID == NpcID.FOREMAN && !talkedWithForeman){
            if (isQuestRunning())
                stopQuest();

            if (!Rs2Walker.walkTo(step.getWorldPoint()))
                return;

            if (!Rs2Dialogue.isInDialogue()){
                Rs2Npc.interact(NpcID.FOREMAN, "Talk-to");
                sleepUntil(Rs2Dialogue::isInDialogue, 5000);

                if (Rs2Dialogue.isInDialogue()){
                    startupQuest();
                    talkedWithForeman = true;
                }
            }
        } else if (step.npcID == NpcID.BLACK_DEMON_1432){
            var safespot = step.getMarkedTiles().get(0).getWorldPoint();

            if (Rs2Player.getWorldLocation().distanceTo(safespot) > 5){
                Rs2Walker.walkTo(safespot, 5);

                if (isQuestRunning())
                    stopQuest();
            }
            else if (!Rs2Player.getWorldLocation().equals(safespot)) {
                Rs2Walker.walkFastCanvas(safespot);

                if (isQuestRunning())
                    stopQuest();
            } else if (!isQuestRunning())
                startupQuest();
        }
    }

    ArrayList<TileObject> handledRoots = new ArrayList<>();
    ArrayList<TileObject> allRoots;

    @Override
    protected void handleObjectStep(ObjectStep step) {
        if (step.objectID == ObjectID.ROOT){
            if (step.getObjects().isEmpty() && allRoots.isEmpty())
                return;

            if (isQuestRunning()){
                allRoots = new ArrayList<>(step.getObjects());
                stopQuest();
                return;
            }

            var root = allRoots.stream().filter(x -> !handledRoots.contains(x)).min(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()))).orElse(null);

            if (Rs2Player.getWorldLocation().distanceTo(root.getWorldLocation()) > 4)
                Rs2Walker.walkTo(root.getWorldLocation());
            else {
                Rs2Walker.setTarget(null);
                sleep(100);
                Rs2GameObject.interact(root, "Search");
                Rs2Player.waitForWalking();
                sleepUntil(Rs2Dialogue::isInDialogue, 2000);

                if (Rs2Dialogue.isInDialogue()){
                    Rs2Dialogue.clickContinue();
                    startupQuest();
                } else {
                    handledRoots.add(root);
                }
            }
        }
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyRequiredItems(
                new ItemRequirement("Wizard hat", ItemID.WIZARD_HAT),
                new ItemRequirement("Zamorak monk top", ItemID.ZAMORAK_MONK_TOP),
                new ItemRequirement("Zamorak monk bottom", ItemID.ZAMORAK_MONK_BOTTOM),
                new ItemRequirement("Amulet of magic", ItemID.AMULET_OF_MAGIC),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS),
                new ItemRequirement("Staff of fire", ItemID.STAFF_OF_FIRE),
                new ItemRequirement("Mind rune", ItemID.MIND_RUNE, 200),
                new ItemRequirement("Air rune", ItemID.AIR_RUNE, 400)
        ))
            return false;

        Rs2Inventory.equip(ItemID.WIZARD_HAT);
        Rs2Inventory.equip(ItemID.ZAMORAK_MONK_TOP);
        Rs2Inventory.equip(ItemID.ZAMORAK_MONK_BOTTOM);
        Rs2Inventory.equip(ItemID.AMULET_OF_MAGIC);
        Rs2Inventory.equip(ItemID.LEATHER_BOOTS);
        Rs2Inventory.equip(ItemID.STAFF_OF_FIRE);

        if (Rs2Bank.isOpen())
            Rs2Bank.closeBank();

        sleep(1000, 2000);
        if (Rs2Tab.getCurrentTab() != InterfaceTab.COMBAT) {
            Rs2Tab.switchToCombatOptionsTab();
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.COMBAT, 2000);
        }

        sleep(100, 200);
        Rs2Widget.clickWidget(WidgetInfo.COMBAT_SPELL_BOX);
        sleepUntil(() -> Rs2Widget.isWidgetVisible(201, 1), 5000);
        var childs = Rs2Widget.getWidget(201, 1).getDynamicChildren();
        Rs2Widget.clickWidgetFast(childs[4], 4);

        return true;
    }
}
