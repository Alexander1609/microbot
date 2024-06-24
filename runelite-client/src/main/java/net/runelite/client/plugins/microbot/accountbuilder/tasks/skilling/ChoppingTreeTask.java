package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.AccountBuilderTask;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.woodcutting.AutoWoodcuttingConfig;
import net.runelite.client.plugins.microbot.woodcutting.AutoWoodcuttingScript;
import net.runelite.client.plugins.microbot.woodcutting.enums.WoodcuttingResetOptions;
import net.runelite.client.plugins.microbot.woodcutting.enums.WoodcuttingTree;

public class ChoppingTreeTask extends AccountBuilderTask {
    AutoWoodcuttingScript script = new AutoWoodcuttingScript();

    AutoWoodcuttingConfig config = new AutoWoodcuttingConfig() {
        @Override
        public WoodcuttingTree TREE() {
            return WoodcuttingTree.TREE;
        }

        @Override
        public WoodcuttingResetOptions resetOptions() {
            return WoodcuttingResetOptions.FIREMAKE;
        }

        @Override
        public int distanceToStray() {
            return 10;
        }
    };

    public ChoppingTreeTask(){
        skill = Skill.WOODCUTTING;
        maxLevel = 15;
        memberOnly = false;
    }

    @Override
    public String getName() {
        return "Woodcutting: Tree";
    }

    @Override
    public boolean doTaskPreparations() {
        if (!Rs2Inventory.hasItem("tinderbox")){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank())
                return false;

            Rs2Bank.depositAll();

            if (Rs2Bank.hasItem("tinderbox"))
                Rs2Bank.withdrawOne("tinderbox");
            else
                cancel();

            return false;
        }

        if (!Rs2Equipment.isWearing("Bronze axe")){
            if (!Rs2Bank.walkToBank() || !Rs2Bank.openBank())
                return false;

            if (Rs2Bank.hasItem("Bronze axe"))
                Rs2Bank.withdrawAndEquip("Bronze axe");
            else
                cancel();
        }

        return Rs2Walker.walkTo(new WorldPoint(3189, 3246, 0), 5);
    }

    @Override
    public void run() {
        script.run(config);
    }

    @Override
    public void doTaskCleanup(boolean shutdown) {
        super.doTaskCleanup(shutdown);

        script.shutdown();
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE && chatMessage.getMessage().contains("you can't light a fire here.")) {
            script.cannotLightFire = true;
        }
    }
}
