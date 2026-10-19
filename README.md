# Player Combat Overhead

Shows **Level 126** (or the actual combat level) above other **player characters**
in Old School RuneScape, using one user-selected text colour. It never enumerates
NPCs. Optional player names look like `Some Player (level-126)`.

Version **1.2.0** displays overhead levels with adjustable text size, colour,
height, bold text, and outline.

## Windows: start the included client

1. Extract the whole ZIP to a normal folder first.
2. Close your other RuneLite window, then double-click **start-plugin.bat**.
3. In the client that opens, search the plugin settings for **Player Combat
   Overhead** and make sure it is enabled.

The script first checks RuneLite's usual bundled Java location, then `JAVA_HOME`,
then Java on your PATH. If it cannot find Java, install a **64-bit Java 17 JDK**
from <https://adoptium.net/> and reopen the script.

This opens a separate RuneLite developer client with the plugin loaded. You must
use that window to see the labels. **Putting the plugin JAR into the normal
RuneLite plugins folder will not install it.** This project has not been submitted
to or approved by the RuneLite Plugin Hub, and no Jagex approval is claimed.

For a **Jagex Account**, development clients need additional setup. Follow
RuneLite's official guide: <https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts>.
The starter does not automate account credentials or change your launcher.

On macOS/Linux, with Java installed, run `sh start-plugin.sh` from a terminal.

## Settings

| Setting | Default | Effect |
| --- | --- | --- |
| Show own level | Off | Also label your own character |
| Include player names | Off | Show the name together with the level |
| Text colour | Yellow | Choose one colour for all level labels |
| Text size | 16 | Size from 10 to 32 pixels |
| Bold text | On | Make the label bolder |
| Black outline | On | Add a one-pixel outline |
| Extra height | 18 | Move labels up or down relative to the head |

If Player Indicators names, skulls, prayers, or health bars overlap the level,
increase **Extra height**, or turn off Player Indicators' overhead names. Labels
are clipped to the game viewport. Players standing on exactly the same spot can
have overlapping labels. They follow RuneLite's scene projection; they do not
perform wall-occlusion checks. Other plugins that hide player models do not
automatically hide these labels.

The plugin reads the current combat level exposed by the client each frame; it
does not use hiscores, send messages, change menus, or automate gameplay. It also
handles player collections in nested worldviews, such as ship scenes. All labels
use the selected text colour. It does not read Wilderness levels or compare
players for attackability.

## Build or modify the source

Requires a Java 17 JDK and an internet connection for the first build. The Gradle
wrapper is included; a separate Gradle installation is unnecessary. The output
uses Java 11-compatible bytecode. RuneLite is pinned to **1.13.2**.

In PowerShell, opened inside this folder:

```powershell
.\gradlew.bat clean test jar launcherDistribution
.\gradlew.bat run
```

On macOS/Linux:

```sh
./gradlew clean test jar launcherDistribution
./gradlew run
```

`run` opens the developer client directly. Build outputs:

- `build/libs/player-combat-overhead-1.2.0.jar`: plugin classes only.
- `build/launcher/`: runnable launcher and its `lib/` dependency folder.

To replace the included launcher after editing, copy the whole `build/launcher/`
directory to `launcher/`. Keep its `lib/` directory alongside the launcher JAR.

After a RuneLite update, change the version in `build.gradle`, or try:

```powershell
.\gradlew.bat -PruneliteVersion=latest.release clean test jar launcherDistribution
```

Review compiler/test failures after an API change. Rebuild the launcher to receive
new RuneLite releases: the bundled launcher itself is a fixed build.

## Validation

See `VALIDATION.txt` for the build and test results for this download. Automated
tests use the actual RuneLite API with mocked players and Java2D pixel rendering.
They are not a live in-game test; no OSRS account was logged into during development.

## Source map

- `PlayerCombatOverheadPlugin.java`: register/remove the overlay.
- `PlayerCombatOverheadOverlay.java`: read players and draw labels.
- `PlayerCombatOverheadConfig.java`: settings in RuneLite's settings panel.
- `DeveloperLauncher.java`: RuneLite's standard external-plugin developer entry point.
- `PlayerCombatOverheadOverlayTest.java`: headless rendering and state tests.

The developer entry point follows RuneLite's official example:
<https://github.com/runelite/example-plugin>.

## Licences

The custom plugin source is provided under the MIT licence in `LICENSE`.
The wrapper and bundled RuneLite/dependency code retain their respective
third-party licences. Upstream notices for Gradle and RuneLite are in `licenses/`;
dependency archives also contain their own notices.
