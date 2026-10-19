@echo off
setlocal
cd /d "%~dp0"
set "PLUGIN_JAVA="
if exist "%LOCALAPPDATA%\RuneLite\jre\bin\java.exe" set "PLUGIN_JAVA=%LOCALAPPDATA%\RuneLite\jre\bin\java.exe"
if not defined PLUGIN_JAVA if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "PLUGIN_JAVA=%JAVA_HOME%\bin\java.exe"
if not defined PLUGIN_JAVA (
    where java.exe >nul 2>nul
    if not errorlevel 1 set "PLUGIN_JAVA=java.exe"
)
if not defined PLUGIN_JAVA (
    echo Java was not found. Install a 64-bit Java 17 JDK from https://adoptium.net/
    echo Then reopen this file. See README.md for details.
    pause
    exit /b 1
)
if not exist "launcher\player-combat-overhead-launcher.jar" (
    echo Build the launcher first: gradlew.bat launcherDistribution
    echo Then copy the whole build\launcher folder to launcher.
    pause
    exit /b 1
)
"%PLUGIN_JAVA%" -ea -jar "launcher\player-combat-overhead-launcher.jar" %*
if errorlevel 1 pause
