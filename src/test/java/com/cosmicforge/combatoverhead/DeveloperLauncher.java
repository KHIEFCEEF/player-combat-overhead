package com.cosmicforge.combatoverhead;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/** Standard RuneLite developer entry point; never packaged into the plugin-only jar. */
public final class DeveloperLauncher
{
    private DeveloperLauncher()
    {
    }

    public static void main(String[] args) throws Exception
    {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (!arguments.contains("--developer-mode"))
        {
            arguments.add("--developer-mode");
        }
        ExternalPluginManager.loadBuiltin(PlayerCombatOverheadPlugin.class);
        RuneLite.main(arguments.toArray(new String[0]));
    }
}
