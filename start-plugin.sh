#!/bin/sh
set -eu
cd "$(dirname "$0")"
exec java -ea -jar launcher/player-combat-overhead-launcher.jar "$@"
