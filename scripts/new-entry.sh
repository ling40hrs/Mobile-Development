#!/usr/bin/env bash
# Scaffold a new homework folder under homeworks/ from homeworks/_template/
#
# Usage: ./scripts/new-entry.sh <N> [Title]
# Example: ./scripts/new-entry.sh 3 "Recycler View Contacts"
# Creates: homeworks/Activity <N>/README.md
set -euo pipefail

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
  echo "Usage: $0 <N> [Title]"
  echo "Example: $0 3 \"Recycler View Contacts\""
  exit 1
fi

NUM="$1"
TITLE="${2:-Title}"

case "$NUM" in
  ''|*[!0-9]*)
    echo "Error: N must be a positive integer (got: $NUM)."
    exit 1
    ;;
esac

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/homeworks/_template"
DEST="$ROOT/homeworks/Activity $NUM"

if [ ! -d "$SRC" ]; then
  echo "Error: template not found at $SRC"
  exit 1
fi

if [ -e "$DEST" ]; then
  echo "Error: destination already exists: $DEST"
  exit 1
fi

mkdir -p "$DEST"

# Fill the template placeholders: the H1 title line and the date.
sed -E \
  -e "s/^# Homework NN .*/# Homework $NUM — $TITLE/" \
  -e "s/^Date:.*/Date: $(date +%F)/" \
  "$SRC/README.md" > "$DEST/README.md"

echo "Created $DEST"
echo
echo "Next:"
echo "  1. Drop in the project files (src/, build.gradle.kts, .gitignore — see AGENTS.md §4)"
echo "  2. Fill in $DEST/README.md — see AGENTS.md §3.2"
echo "  3. Add a Homeworks row in $ROOT/README.md (YYYY-MM-DD, Canvas link) — AGENTS.md §3.1"
