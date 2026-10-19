package com.cosmicforge.combatoverhead;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.Text;

@Singleton
public class PlayerCombatOverheadOverlay extends Overlay
{
    private static final int HEAD_MARGIN = 40;
    private final Client client;
    private final PlayerCombatOverheadConfig config;

    @Inject
    PlayerCombatOverheadOverlay(Client client, PlayerCombatOverheadConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return null;
        }

        Player localPlayer = client.getLocalPlayer();
        WorldView worldView = client.getTopLevelWorldView();
        if (localPlayer == null || worldView == null)
        {
            return null;
        }

        // A separate graphics context prevents our font/colour/clip from
        // affecting RuneLite's other overlays.
        Graphics2D textGraphics = (Graphics2D) graphics.create();
        try
        {
            textGraphics.setFont(new Font(Font.SANS_SERIF,
                config.boldText() ? Font.BOLD : Font.PLAIN,
                Math.max(10, Math.min(32, config.fontSize()))));
            textGraphics.clipRect(client.getViewportXOffset(), client.getViewportYOffset(),
                client.getViewportWidth(), client.getViewportHeight());
            renderWorldView(textGraphics, worldView, localPlayer);
        }
        finally
        {
            textGraphics.dispose();
        }
        return null;
    }

    private void renderWorldView(Graphics2D graphics, WorldView worldView, Player localPlayer)
    {
        // This collection contains players only. NPCs are never inspected.
        for (Player player : worldView.players())
        {
            if (player == null || (player == localPlayer && !config.showOwnLevel()))
            {
                continue;
            }

            int combatLevel = player.getCombatLevel();
            if (combatLevel <= 0 || player.getWorldLocation().getPlane() != worldView.getPlane())
            {
                continue;
            }

            String text = "Level " + combatLevel;
            if (config.showNames() && player.getName() != null)
            {
                text = Text.sanitize(player.getName()) + " (level-" + combatLevel + ")";
            }

            Point anchor = player.getCanvasTextLocation(graphics, text,
                player.getLogicalHeight() + HEAD_MARGIN);
            if (anchor == null)
            {
                continue;
            }

            int x = anchor.getX();
            int y = anchor.getY() - Math.max(-50, Math.min(150, config.verticalOffset()));
            if (config.outlineText())
            {
                graphics.setColor(Color.BLACK);
                for (int dx = -1; dx <= 1; dx++)
                {
                    for (int dy = -1; dy <= 1; dy++)
                    {
                        if (dx != 0 || dy != 0)
                        {
                            graphics.drawString(text, x + dx, y + dy);
                        }
                    }
                }
            }
            graphics.setColor(config.textColor());
            graphics.drawString(text, x, y);
        }

        // Players on ships and other nested scenes have their own worldview.
        for (WorldView child : worldView.worldViews())
        {
            if (child != null)
            {
                renderWorldView(graphics, child, localPlayer);
            }
        }
    }
}
