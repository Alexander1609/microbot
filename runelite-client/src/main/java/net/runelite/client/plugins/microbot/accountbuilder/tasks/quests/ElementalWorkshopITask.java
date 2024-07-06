package net.runelite.client.plugins.microbot.accountbuilder.tasks.quests;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

public class ElementalWorkshopITask extends AccountBuilderQuestTask {
    public ElementalWorkshopITask(){
        super(QuestHelperQuest.ELEMENTAL_WORKSHOP_I,
                new ItemRequirement("Rune scimitar", ItemID.RUNE_SCIMITAR, 1, true),
                new ItemRequirement("Mithril chainbody", ItemID.MITHRIL_CHAINBODY, 1, true),
                new ItemRequirement("Leather boots", ItemID.LEATHER_BOOTS, 1, true),
                new ItemRequirement("Leather gloves", ItemCollections.COMBAT_BRACELETS, 1, true),
                new ItemRequirement("Mithril full helm", ItemID.MITHRIL_FULL_HELM, 1, true),
                new ItemRequirement("Mithril kiteshield", ItemID.MITHRIL_KITESHIELD, 1, true),
                new ItemRequirement("Mithril platelegs", ItemID.MITHRIL_PLATELEGS, 1, true));
    }

    @Override
    public boolean doTaskPreparations() {
        return clearInventory() && withdrawBuyItems();
    }
}
