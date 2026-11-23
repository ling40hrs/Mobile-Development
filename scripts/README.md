# Scripts

Helpers for this archive.

- `new-entry.sh` — scaffold a new homework folder from `homeworks/_template/`
  ```bash
  ./scripts/new-entry.sh 3 "Recycler View Contacts"
  # → homeworks/Activity 3/README.md  (title + today's date pre-filled)
  ```
  Takes the activity number and an optional title. Fails if the folder already
  exists. You still need to add the project files, fill in the README, and add a
  row to the course `README.md` table.
