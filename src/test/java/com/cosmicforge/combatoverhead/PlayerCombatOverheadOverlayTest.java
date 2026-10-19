package com.cosmicforge.combatoverhead;

import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedObjectSet;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PlayerCombatOverheadOverlayTest
{
    private final Client client = mock(Client.class);
    private final WorldView view = mock(WorldView.class);
    private final Player self = player(76);
    private final Player other = player(126);
    private final Settings config = new Settings();
    private final BufferedImage image = new BufferedImage(400, 240, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D graphics = image.createGraphics();
    private final PlayerCombatOverheadOverlay overlay = new PlayerCombatOverheadOverlay(client, config);

    private static class Settings implements PlayerCombatOverheadConfig
    {
        boolean own;
        boolean names;
        int offset = 18;
        Color colour = Color.YELLOW;

        @Override public boolean showOwnLevel() { return own; }
        @Override public boolean showNames() { return names; }
        @Override public int verticalOffset() { return offset; }
        @Override public Color textColor() { return colour; }
    }

    @Before
    public void setUp()
    {
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getLocalPlayer()).thenReturn(self);
        when(client.getTopLevelWorldView()).thenReturn(view);
        when(client.getViewportWidth()).thenReturn(400);
        when(client.getViewportHeight()).thenReturn(240);
        doReturn(objects(self, other)).when(view).players();
        doReturn(objects()).when(view).worldViews();
    }

    @After
    public void disposeGraphics()
    {
        graphics.dispose();
    }

    private void clearImage()
    {
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setComposite(AlphaComposite.SrcOver);
    }

    private void assertTextColor(Color expected)
    {
        boolean found = false;
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int rgba = image.getRGB(x, y);
                if ((rgba >>> 24) != 0 && (rgba & 0xffffff) != 0)
                {
                    assertEquals("All foreground text pixels should have the expected colour", expected.getRGB(), rgba);
                    found = true;
                }
            }
        }
        assertTrue("Expected visible text in the selected colour", found);
    }

    private static Player player(int level)
    {
        Player player = mock(Player.class);
        when(player.getCombatLevel()).thenReturn(level);
        when(player.getWorldLocation()).thenReturn(new WorldPoint(3200, 3200, 0));
        when(player.getLogicalHeight()).thenReturn(180);
        when(player.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt()))
            .thenReturn(new Point(80, 100));
        return player;
    }

    @SafeVarargs
    private static <T> IndexedObjectSet<T> objects(T... elements)
    {
        @SuppressWarnings("unchecked")
        IndexedObjectSet<T> set = mock(IndexedObjectSet.class);
        when(set.iterator()).thenAnswer(invocation -> Arrays.asList(elements).iterator());
        return set;
    }

    private boolean painted()
    {
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                if ((image.getRGB(x, y) >>> 24) != 0) { return true; }
            }
        }
        return false;
    }

    @Test
    public void drawsOtherPlayersLevelWithoutNamesOrNpcLookups()
    {
        overlay.render(graphics);
        assertTrue("Expected actual text pixels", painted());
        verify(other).getCanvasTextLocation(any(Graphics2D.class), eq("Level 126"), eq(220));
        verify(self, never()).getCanvasTextLocation(any(), anyString(), anyInt());
        verify(client, never()).getNpcs();
        verify(view, never()).npcs();
    }

    @Test
    public void ownLevelCanBeEnabled()
    {
        doReturn(objects(self)).when(view).players();
        overlay.render(graphics);
        assertFalse(painted());
        config.own = true;
        overlay.render(graphics);
        assertTrue(painted());
        verify(self).getCanvasTextLocation(any(Graphics2D.class), eq("Level 76"), eq(220));
    }

    @Test
    public void loginScreenAndMissingWorldDoNotRender()
    {
        when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
        overlay.render(graphics);
        assertFalse(painted());
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getTopLevelWorldView()).thenReturn(null);
        overlay.render(graphics);
        assertFalse(painted());
        when(client.getTopLevelWorldView()).thenReturn(view);
        when(client.getLocalPlayer()).thenReturn(null);
        overlay.render(graphics);
        assertFalse(painted());
    }

    @Test
    public void offscreenAndUnavailableLevelsAreSkipped()
    {
        when(other.getCanvasTextLocation(any(Graphics2D.class), anyString(), anyInt())).thenReturn(null);
        overlay.render(graphics);
        assertFalse(painted());
        when(other.getCombatLevel()).thenReturn(0);
        overlay.render(graphics);
        assertFalse(painted());
    }

    @Test
    public void playersOnAnotherPlaneAreSkipped()
    {
        when(other.getWorldLocation()).thenReturn(new WorldPoint(3200, 3200, 1));
        overlay.render(graphics);
        assertFalse(painted());
    }

    @Test
    public void playersInNestedWorldviewsAreRendered()
    {
        WorldView child = mock(WorldView.class);
        doReturn(objects(self)).when(view).players();
        doReturn(objects(child)).when(view).worldViews();
        doReturn(objects(other)).when(child).players();
        doReturn(objects()).when(child).worldViews();
        overlay.render(graphics);
        assertTrue(painted());
    }

    @Test
    public void namesAndLevelUpdatesUseCurrentPlayerData()
    {
        config.names = true;
        when(other.getName()).thenReturn("Some Player");
        when(other.getCombatLevel()).thenReturn(99);
        overlay.render(graphics);
        verify(other).getCanvasTextLocation(any(Graphics2D.class), eq("Some Player (level-99)"), eq(220));
        when(other.getCombatLevel()).thenReturn(100);
        overlay.render(graphics);
        verify(other).getCanvasTextLocation(any(Graphics2D.class), eq("Some Player (level-100)"), eq(220));
    }

    @Test
    public void graphicsStateIsPreservedAndDrawingStaysInsideViewport()
    {
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        graphics.setFont(font);
        graphics.setColor(Color.MAGENTA);
        when(client.getViewportWidth()).thenReturn(50);
        overlay.render(graphics);
        assertFalse("Text outside the viewport must be clipped", painted());
        assertEquals(font, graphics.getFont());
        assertEquals(Color.MAGENTA, graphics.getColor());
        assertNull(graphics.getClip());
    }

    @Test
    public void verticalOffsetMovesTextUpOnScreen()
    {
        config.offset = 40;
        overlay.render(graphics);
        boolean paintedAboveAnchor = false;
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                if ((image.getRGB(x, y) >>> 24) != 0)
                {
                    assertTrue("Pixels should stay at or above the shifted baseline", y <= 61);
                    paintedAboveAnchor = true;
                }
            }
        }
        assertTrue(paintedAboveAnchor);
    }

    @Test
    public void selectedColourIsUsedForEveryCombatLevel()
    {
        config.colour = Color.CYAN;
        for (int level : new int[] {3, 50, 76, 100, 126})
        {
            clearImage();
            when(other.getCombatLevel()).thenReturn(level);
            overlay.render(graphics);
            assertTextColor(Color.CYAN);
        }
        verify(client, never()).getVarbitValue(anyInt());
        verify(client, never()).getWidget(anyInt());
    }

    @Test
    public void chosenTextColourUpdatesImmediately()
    {
        config.colour = Color.ORANGE;
        overlay.render(graphics);
        assertTextColor(Color.ORANGE);
        clearImage();
        config.colour = Color.BLUE;
        when(self.getCombatLevel()).thenReturn(126);
        when(other.getCombatLevel()).thenReturn(100);
        overlay.render(graphics);
        assertTextColor(Color.BLUE);
    }
}
