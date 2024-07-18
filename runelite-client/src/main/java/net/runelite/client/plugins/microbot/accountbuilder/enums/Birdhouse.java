package net.runelite.client.plugins.microbot.accountbuilder.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum Birdhouse {
    REGULAR(ItemID.LOGS, 5, 5),
    OAK(ItemID.OAK_LOGS, 15, 14),
    WILLOW(ItemID.WILLOW_LOGS, 25, 24),
    TEAK(ItemID.TEAK_LOGS, 35, 34),
    MAPLE(ItemID.MAPLE_LOGS, 45, 44),
    MAHOGANY(ItemID.MAHOGANY_LOGS, 50, 49),
    YEW(ItemID.YEW_LOGS, 60, 59),
    MAGIC(ItemID.MAGIC_LOGS, 75, 74),
    REDWOOD(ItemID.REDWOOD_LOGS, 90, 89);

    private final int logsId;
    private final int craftingLevel;
    private final int hunterLevel;
}
