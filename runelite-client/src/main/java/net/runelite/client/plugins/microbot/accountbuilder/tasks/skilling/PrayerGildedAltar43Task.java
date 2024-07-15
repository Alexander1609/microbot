package net.runelite.client.plugins.microbot.accountbuilder.tasks.skilling;

import net.runelite.api.ItemID;
import net.runelite.api.Skill;

public class PrayerGildedAltar43Task extends PrayerGildedAltarTask {
    @Override
    public String getName() {
        return "Prayer: Gilded altar (43)";
    }

    public PrayerGildedAltar43Task(){
        super(ItemID.WYRMLING_BONES, 480);

        skill = Skill.PRAYER;
        minLevel = 33;
        maxLevel = 43;
    }
}
