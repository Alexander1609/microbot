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
}
