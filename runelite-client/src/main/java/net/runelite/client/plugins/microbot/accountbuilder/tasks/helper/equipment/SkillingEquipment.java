package net.runelite.client.plugins.microbot.accountbuilder.tasks.helper.equipment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;

@Getter
@RequiredArgsConstructor
public enum SkillingEquipment {
    BRONZE_AXE(ItemID.BRONZE_AXE, "Bronze axe", Skill.WOODCUTTING, 1, 0),
    MITHRIL_AXE(ItemID.MITHRIL_AXE, "Mithril axe", Skill.WOODCUTTING, 21, 300);

    private final int id;
    private final String name;
    private final Skill skill;
    private final int level;
    private final int gp;
}
