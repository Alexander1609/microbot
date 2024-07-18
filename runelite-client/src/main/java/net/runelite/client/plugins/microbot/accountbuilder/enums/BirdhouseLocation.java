package net.runelite.client.plugins.microbot.accountbuilder.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.timetracking.hunter.BirdHouseSpace;

@Getter
@AllArgsConstructor
public enum BirdhouseLocation {
    MEADOW_NORTH(BirdHouseSpace.MEADOW_NORTH, 30565, new WorldPoint(3677, 3882, 0)),
    MEADOW_SOUTH(BirdHouseSpace.MEADOW_SOUTH, 30566, new WorldPoint(3679, 3815, 0)),
    VALLEY_NORTH(BirdHouseSpace.VALLEY_NORTH, 30567, new WorldPoint(3768, 3761, 0)),
    VALLEY_SOUTH(BirdHouseSpace.VALLEY_SOUTH, 30568, new WorldPoint(3763, 3755, 0));

    private final BirdHouseSpace space;
    private final int objectId;
    private final WorldPoint worldPoint;
}
