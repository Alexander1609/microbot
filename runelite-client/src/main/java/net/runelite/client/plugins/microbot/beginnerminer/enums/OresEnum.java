package net.runelite.client.plugins.microbot.beginnerminer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OresEnum {
    COPPER("Copper rocks", "Copper ore"),
    TIN("Tin rocks", "Tin ore");

    private final String rockName;
    private final String oreName;
}
