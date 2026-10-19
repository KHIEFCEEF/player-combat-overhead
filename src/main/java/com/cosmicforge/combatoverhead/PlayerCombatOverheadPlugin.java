package com.cosmicforge.combatoverhead;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
    name = "Player Combat Overhead",
    description = "Displays other players' combat levels above their characters",
    tags = {"player", "combat", "level", "overhead"},
    enabledByDefault = true
)
public class PlayerCombatOverheadPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PlayerCombatOverheadOverlay overlay;

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
    }

    @Provides
    PlayerCombatOverheadConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PlayerCombatOverheadConfig.class);
    }
}
