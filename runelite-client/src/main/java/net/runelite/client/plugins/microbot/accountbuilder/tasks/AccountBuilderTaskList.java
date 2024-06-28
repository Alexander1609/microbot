package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Getter;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.GiantFrogFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.GoblinFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.moneymaking.CollectingBronzePickaxesTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.*;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.*;

import java.util.HashMap;
import java.util.Map;

public class AccountBuilderTaskList {
    @Getter
    private static final Map<AccountBuilderTask, Integer> tasks;

    static {
        tasks = new HashMap<>();

        tasks.put(new CookAssistantTask(), 4000);
        tasks.put(new SheepShearerTask(), 2000);
        tasks.put(new RomeoJulietTask(), 1000);
        tasks.put(new RuneMysteriesTask(), 1000);
        tasks.put(new RestlessGhostTask(), 1000);
        tasks.put(new ErnestTheChickenTask(), 1000);
        tasks.put(new MisthalinMysteryTask(), 1000);
        tasks.put(new VampyreSlayerTask(), 1000);
        tasks.put(new BelowIceMountainTask(), 10000);
        tasks.put(new ImpCatcherTask(), 1000);
        tasks.put(new BlackKnightFortressTask(), 1000);
        tasks.put(new GoblinDiplomacyTask(), 1000);
        tasks.put(new DoricsQuestTask(), 1000);
        tasks.put(new PrinceAliRescueTask(), 1000);
        tasks.put(new WitchsPotionTask(), 1000);

        tasks.put(new NatualHistoryQuizTask(), 1000);
        tasks.put(new DwarfCannonTask(), 1000);
        tasks.put(new WaterfallQuestTask(), 1000);
        tasks.put(new ChildrenOfTheSunTask(), 1000);
        tasks.put(new TreeGnomeVillageTask(), 1000);
        tasks.put(new KnightsSwordTask(), 1000);
        tasks.put(new GertrudesCatTask(), 1000);
        tasks.put(new DruidicRitualTask(), 1000);
        tasks.put(new TouristTrapTask(), 1000);
        tasks.put(new RecruitmentDriveTask(), 1000);
        tasks.put(new SleepingGiantsTask(), 1000);
        tasks.put(new DaddysHomeTask(), 1000);

        tasks.put(new GoblinFightingTask(), 1000);
        tasks.put(new GiantFrogFightingTask(), 1000);

        tasks.put(new MiningSmithingBronzeTask(), 1000);
        tasks.put(new ChoppingTreeTask(), 1000);
        tasks.put(new ChoppingOakTask(), 1000);
        tasks.put(new ChoppingWillowTask(), 1000);
        tasks.put(new FishingShrimpsTask(), 1000);
        tasks.put(new FletchingHeadlessArrowTask(), 1000);

        tasks.put(new CollectingBronzePickaxesTask(), 1000);
    }
}
