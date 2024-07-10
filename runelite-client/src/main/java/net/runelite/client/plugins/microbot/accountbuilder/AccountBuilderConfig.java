package net.runelite.client.plugins.microbot.accountbuilder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.time.Duration;

@ConfigGroup("example")
public interface AccountBuilderConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Debug",
            name = "Debug",
            description = "Debug",
            position = 0,
            section = generalSection
    )
    default boolean debugMode() { return false; }

    @ConfigItem(
            keyName = "Is Member",
            name = "Is Member",
            description = "use Member worlds",
            position = 0,
            section = generalSection
    )
    default boolean isMember() { return false; }

    @ConfigItem(
            keyName = "MinTaskDuration",
            name = "Min task duration",
            description = "in minutes",
            position = 0,
            section = generalSection
    )
    default int MinTaskDuration() { return 20; }

    @ConfigItem(
            keyName = "MaxTaskDuration",
            name = "Max task duration",
            description = "in minutes",
            position = 0,
            section = generalSection
    )
    default int MaxTaskDuration() { return 60; }

    @ConfigSection(
            name = "Breaks",
            description = "Breaks",
            position = 1,
            closedByDefault = true
    )
    String breakSection = "breaks";

    @ConfigItem(
            keyName = "TimeUntilBreakStart",
            name = "Time break start",
            description = "Time until break start in minutes",
            position = 0,
            section = breakSection
    )
    default int timeUntilBreakStart() {
        return 60;
    }
    @ConfigItem(
            keyName = "TimeUntilBreakEnd",
            name = "Time break end",
            description = "Time until break ends in minutes",
            position = 1,
            section = breakSection
    )
    default int timeUntilBreakEnd() {
        return 120;
    }

    @ConfigItem(
            keyName = "BreakDurationStart",
            name = "Break duration start",
            description = "Break duration start in minutes",
            position = 2,
            section = breakSection
    )
    default int breakDurationStart() {
        return 10;
    }
    @ConfigItem(
            keyName = "BreakDurationEnd",
            name = "Break duration end",
            description = "Break duration end in minutes",
            position = 3,
            section = breakSection
    )
    default int breakDurationEnd() {
        return 15;
    }
}
