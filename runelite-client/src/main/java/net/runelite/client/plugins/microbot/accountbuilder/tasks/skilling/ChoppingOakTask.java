package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
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

public class ChoppingOakTask extends AccountBuilderTask {
    AutoWoodcuttingScript script = new AutoWoodcuttingScript();

    AutoWoodcuttingConfig config = new AutoWoodcuttingConfig() {
        @Override
        public WoodcuttingTree TREE() {
            return WoodcuttingTree.OAK;
        }

        @Override
        public WoodcuttingResetOptions resetOptions() {
            return WoodcuttingResetOptions.FIREMAKE;
        }
    };

    public ChoppingOakTask(){
        skill = Skill.WOODCUTTING;
        minLevel = 15;
        maxLevel = 35;
        memberOnly = false;
    }

    @Override
    public String getName() {
        return "Woodcutting: Oak";
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

        return Rs2Walker.walkTo(new WorldArea(3239, 3265, 6, 6, 0), 5);
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
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE && chatMessage.getMessage().equals("You can't light a fire here.")) {
            script.cannotLightFire = true;
        }
    }
}
