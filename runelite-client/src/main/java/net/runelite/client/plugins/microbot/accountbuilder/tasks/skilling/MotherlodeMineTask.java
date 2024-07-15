package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.mining.motherloadmine.MotherloadMineConfig;
import net.runelite.client.plugins.microbot.mining.motherloadmine.MotherloadMineScript;
import net.runelite.client.plugins.microbot.mining.motherloadmine.enums.MLMStatus;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class MotherlodeMineTask extends AccountBuilderTask {
    @Override
    public String getName() {
        return "Mining: Motherlode mine";
    }

    MotherloadMineScript motherloadMineScript = new MotherloadMineScript();
    MotherloadMineConfig motherloadMineConfig = new MotherloadMineConfig() {
        @Override
        public boolean pickAxeInInventory() {
            return true;
        }

        @Override
        public boolean mineUpstairs() {
            return false;
        }
    };

    public MotherlodeMineTask(){
        skill = Skill.MINING;
        minLevel = 31;
        maxLevel = 70;

        itemRequirements.add(new ItemRequirement("Adamant pick", ItemID.ADAMANT_PICKAXE, 1));
        itemRequirements.add(new ItemRequirement("Hammer", ItemID.HAMMER, 1));

        // Ardy cloak for leaving tp
        itemRequirements.add(new ItemRequirement("Ardy cape", ItemCollections.ARDY_CLOAKS, 1, true));
    }

    @Override
    public void run() {
        super.run();

        motherloadMineScript.run(motherloadMineConfig);
    }

    @Override
    public boolean doTaskPreparations() {
        if (!clearInventory() || !withdrawBuyItems())
            return false;

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3706, 5630, 72, 69, 0)) != 0
            && !Rs2Walker.walkTo(new WorldPoint(3757, 5666, 0)))
            return false;

        if (Rs2Player.getWorldLocation().distanceTo(new WorldArea(3710, 5675, 32, 25, 0)) == 0){
            Rs2Walker.setTarget(null);
            var firstStone = Rs2GameObject.getGameObject(new WorldPoint(3731, 5683, 0));
            if (firstStone != null){
                Rs2GameObject.interact(firstStone, "Mine");
                Rs2Player.waitForAnimation();
            }
            Rs2Walker.walkFastCanvas(new WorldPoint(3732, 5681, 0));
            Rs2Player.waitForWalking();

            var secondStone = Rs2GameObject.getGameObject(new WorldPoint(3733, 5680, 0));
            if (secondStone != null){
                Rs2GameObject.interact(secondStone, "Mine");
                Rs2Player.waitForAnimation();
            }
            Rs2Walker.walkFastCanvas(new WorldPoint(3746, 5674, 0));
            Rs2Player.waitForWalking();

            return false;
        }

        return true;
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        // Wait until all paydirt has been deposited
        var paydirtPresent = Rs2Inventory.contains(ItemID.PAYDIRT);
        while (paydirtPresent){
            sleepUntil(() -> !Rs2Inventory.contains(ItemID.PAYDIRT), 60_000);

            // Validate
            sleep(500);
            if (!Rs2Inventory.contains(ItemID.PAYDIRT))
                paydirtPresent = false;
        }

        super.doTaskCleanup(shutdown);

        motherloadMineScript.shutdown();

        Rs2Walker.walkTo(new WorldPoint(2604, 3228, 0));
        Rs2Player.waitForWalking();
    }

    @Override
    public void onWallObjectSpawned(WallObjectSpawned event) {
        WallObject wallObject = event.getWallObject();
        try {
            if (wallObject == null || MotherloadMineScript.oreVein == null)
                return;
            if (MotherloadMineScript.status == MLMStatus.MINING && (wallObject.getId() == ObjectID.DEPLETED_VEIN_26665 || wallObject.getId() == ObjectID.DEPLETED_VEIN_26666 || wallObject.getId() == ObjectID.DEPLETED_VEIN_26667 || wallObject.getId() == ObjectID.DEPLETED_VEIN_26668)) {
                if (wallObject.getWorldLocation().equals(MotherloadMineScript.oreVein.getWorldLocation())) {
                    MotherloadMineScript.oreVein = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIdleMove() {
        motherloadMineScript.oreVein = null;
    }
}
