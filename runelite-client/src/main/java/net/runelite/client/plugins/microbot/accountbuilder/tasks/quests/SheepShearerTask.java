package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.NpcStep;

import java.util.Arrays;

public class SheepShearerTask extends AccountBuilderQuestTask {
    WorldArea sheepArea = new WorldArea(3193, 3258, 18, 17, 0);
    boolean woolDone = false;

    public SheepShearerTask(){
        super(QuestHelperQuest.SHEEP_SHEARER);
        memberOnly = false;
    }

    @Override
    protected void handleNPCStep(NpcStep step) {
        if (!woolDone){
            var woolCountRequired = ((ItemRequirement)step.getRequirements().get(0)).getQuantity();

            if (Rs2Inventory.contains("Shears") && woolCountRequired > Rs2Inventory.count("Ball of wool")){
                if (isQuestRunning())
                    stopQuest();

                if (Rs2Inventory.count("Wool") < woolCountRequired){
                    if (!Rs2Walker.walkTo(sheepArea, 1))
                        return;

                    var sheep = Rs2Npc.getNpcs("Sheep", true)
                            .filter(x -> Arrays.asList(Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getNpcDefinition(x.getId())).getActions()).contains(("Shear")))
                            .findFirst().orElse(null);

                    if (sheep != null){
                        Rs2Npc.interact(sheep, "Shear");
                        Rs2Player.waitForAnimation();
                    }
                } else {
                    if (!Rs2Walker.walkTo(new WorldPoint(3209, 3213, 1), 1))
                        return;

                    Rs2GameObject.interact("Spinning wheel", "Spin");
                    sleepUntil(() -> Rs2Widget.getWidget(17694734) != null, 5000);
                    Rs2Widget.clickWidget(17694734);

                    sleep(5000);
                    while (true) {
                        var woolCount = Rs2Inventory.get("Ball of wool").quantity;
                        sleep(3000);
                        if (woolCount == Rs2Inventory.get("Ball of wool").quantity)
                            break;
                    }
                }
            }
            else if (woolCountRequired == Rs2Inventory.count("Ball of wool") && Rs2Walker.walkTo(new WorldPoint(3188, 3273, 0), 2))
                woolDone = true;
        }
        else if (!isQuestRunning())
            startupQuest();
    }

    @Override
    public boolean doTaskPreparations() {
        if (Rs2Inventory.getEmptySlots() < 21){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank() || !Rs2Bank.isOpen())
                return false;

            Rs2Bank.depositAll();
            return false;
        }

        return Rs2Walker.walkTo(new WorldPoint(3188, 3273, 0), 2);
    }
}
