package com.cosmicforge.combatoverhead;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("playercombatoverhead")
public interface PlayerCombatOverheadConfig extends Config
{
    @ConfigItem(keyName = "showOwnLevel", name = "Show own level",
        description = "Also display a combat level above your own character", position = 0)
    default boolean showOwnLevel()
    {
        return false;
    }

    @ConfigItem(keyName = "showNames", name = "Include player names",
        description = "Show Player name (level-126) instead of Level 126", position = 1)
    default boolean showNames()
    {
        return false;
    }

    @ConfigItem(keyName = "textColor", name = "Text colour",
        description = "Colour used for all combat-level labels", position = 2)
    default Color textColor()
    {
        return Color.YELLOW;
    }

    @Range(min = 10, max = 32)
    @ConfigItem(keyName = "fontSize", name = "Text size",
        description = "Font size in pixels", position = 3)
    default int fontSize()
    {
        return 16;
    }

    @ConfigItem(keyName = "boldText", name = "Bold text",
        description = "Use bold text", position = 4)
    default boolean boldText()
    {
        return true;
    }

    @ConfigItem(keyName = "outlineText", name = "Black outline",
        description = "Draw a black outline to improve readability", position = 5)
    default boolean outlineText()
    {
        return true;
    }

    @Range(min = -50, max = 150)
    @ConfigItem(keyName = "verticalOffset", name = "Extra height",
        description = "Extra screen pixels above the head; increase if names or icons overlap", position = 6)
    default int verticalOffset()
    {
        return 18;
    }

}
