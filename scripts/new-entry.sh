#!/usr/bin/env bash
# Scaffold a new activity / homework / project from its _template/
# Usage: ./scripts/new-entry.sh <activities|homeworks|projects> <name-kebab-case>
# Example: ./scripts/new-entry.sh activities activity-01-hello-world
set -euo pipefail

if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <activities|homeworks|projects> <folder-name>"
  echo "Example: $0 activities activity-01-hello-world"
  exit 1
fi

TYPE="$1"
NAME="$2"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

case "$TYPE" in
  activities|homeworks|projects) ;;
  *) echo "Error: TYPE must be activities, homeworks, or projects."; exit 1 ;;
esac

SRC="$ROOT/$TYPE/_template"
DEST="$ROOT/$TYPE/$NAME"

if [ ! -d "$SRC" ]; then
  echo "Error: template not found at $SRC"
  exit 1
fi

if [ -e "$DEST" ]; then
  echo "Error: destination already exists: $DEST"
  exit 1
fi

cp -r "$SRC" "$DEST"
mkdir -p "$DEST/screenshots"

echo "Created $DEST from $SRC"
echo "Next: edit $DEST/README.md"
