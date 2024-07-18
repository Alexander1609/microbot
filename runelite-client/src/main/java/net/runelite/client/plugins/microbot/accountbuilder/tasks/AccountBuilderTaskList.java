package net.runelite.client.plugins.microbot.accountbuilder.tasks;

import lombok.Getter;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.AmmoniteCrabFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.GiantFrogFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.fighting.GoblinFightingTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.moneymaking.CollectingBronzePickaxesTask;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.quests.*;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling.*;
import net.runelite.client.plugins.microbot.accountbuilder.tasks.utility.*;

import java.util.HashMap;
import java.util.Map;

public class AccountBuilderTaskList {
    @Getter
    private static final Map<AccountBuilderTask, Integer> tasks;

    static {
        tasks = new HashMap<>();

        // F2P Quests
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
        tasks.put(new PiratesTreasureTask(), 1000);

        // P2P Quests
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
        tasks.put(new GrandTreeTask(), 1000);
        tasks.put(new MonksFriendTask(), 1000);
        tasks.put(new PlagueCityTask(), 1000);
        tasks.put(new BiohazardTask(), 1000);
        tasks.put(new HazeelCultTask(), 1000);
        //tasks.put(new MurderMysteryTask(), 1000);
        tasks.put(new EnterTheAbyssTask(), 1000);
        tasks.put(new PriestInPerilTask(), 1000);
        tasks.put(new FightArenaTask(), 1000);
        tasks.put(new TribalTotemTask(), 1000);
        tasks.put(new NatureSpiritTask(), 1000);
        tasks.put(new ElementalWorkshopITask(), 1000);
        tasks.put(new ElementalWorkshopIITask(), 1000);
        tasks.put(new TowerOfLifeTask(), 1000);
        tasks.put(new MerlinsCrystalTask(), 1000);
        tasks.put(new RFDStartTask(), 1000);
        tasks.put(new RFDGobilGeneralsTask(), 1000);
        tasks.put(new DeathPlateauTask(), 1000);
        tasks.put(new HolyGrailTask(), 1000);
        tasks.put(new LostTribeTask(), 1000);
        tasks.put(new LostCityTask(), 1000);
        tasks.put(new FairytaleITask(), 1000);
        tasks.put(new FairytaleIIUnlockTask(), 1000);
        tasks.put(new DeathToTheDorgeshuunTask(), 1000);
        tasks.put(new SeaSlugTask(), 1000);
        tasks.put(new FishingContestTask(), 1000);
        tasks.put(new RFDMountainDwarfTask(), 1000);
        tasks.put(new MountainDaughterTask(), 1000);
        tasks.put(new XMarksTheSpotTask(), 1000);
        tasks.put(new ClientOfKourendTask(), 1000);
        tasks.put(new TheGiantDwarfTask(), 1000);
        tasks.put(new TheQueenOfThievesTask(), 1000);
        tasks.put(new JunglePotionTask(), 1000);
        tasks.put(new TheCorsairCurseTask(), 1000);
        //tasks.put(new DemonSlayerTask(), 1000);
        tasks.put(new AlfredGrimhandsBarcrawlTask(), 1000);
        tasks.put(new TaiBwoWannaiTrioTask(), 1000);
        tasks.put(new TheEyesOfGlouphrieTask(), 1000);
        tasks.put(new TempleOfTheEyeTask(), 1000);
        tasks.put(new TheDigSiteTask(), 1000);
        tasks.put(new APorcineOfInterestTask(), 1000);
        tasks.put(new BoneVoyageTask(), 1000);

        // Diaries
        tasks.put(new ArdougneEasyDiaryTask(), 1000);
        tasks.put(new KandarinEasyDiaryTask(), 1000);

        // Combat
        tasks.put(new GoblinFightingTask(), 1000);
        tasks.put(new GiantFrogFightingTask(), 1000);
        tasks.put(new AmmoniteCrabFightingTask(), 1000);

        // Skilling
        tasks.put(new MiningSmithingBronzeTask(), 1000);
        tasks.put(new ChoppingTreeTask(), 1000);
        tasks.put(new ChoppingOakTask(), 1000);
        tasks.put(new ChoppingWillowTask(), 1000);
        tasks.put(new FishingShrimpsTask(), 1000);
        tasks.put(new FletchingHeadlessArrowTask(), 1000);
        tasks.put(new BuildingCrudeChairTask(), 1000);
        tasks.put(new MotherlodeMineTask(), 1000);
        tasks.put(new AgilityVarrockTask(), 1000);
        tasks.put(new AgilityBarbarianOutpostTask(), 1000);
        tasks.put(new AgilityShayzienTask(), 1000);
        tasks.put(new WintertodtTask(), 1000);
        tasks.put(new CraftingGlassblowingTask(), 1000);
        tasks.put(new FletchingWillowLongbowTask(), 1000);
        tasks.put(new FletchingMapleShortbowTask(), 1000);
        tasks.put(new FletchingMapleLongbowTask(), 1000);
        tasks.put(new SmithingGiantsFoundrySteelTask(), 1000);
        tasks.put(new SmithingGiantsFoundryMithrilTask(), 1000);
        tasks.put(new PrayerGildedAltar43Task(), 1000);
        tasks.put(new CookingKarambwanTask(), 1000);
        tasks.put(new BirdhouseTask(), 1000);

        // Money making
        tasks.put(new CollectingBronzePickaxesTask(), 1000);

        // Utility
        tasks.put(new IceGlovesTask(), 1000);
        tasks.put(new Kudos100Task(), 1000);
    }
}
