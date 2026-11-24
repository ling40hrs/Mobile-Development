# Testing

Date: 2026-11-24

- Device: Pixel 8 AVD, Android 17 (API 37), 720 × 1280 at 320 dpi
- Builds: `app-debug.apk` and the signed `app-release.apk` (version 1.0.0)
- Unit tests: `./gradlew test` → 27 tests, 0 failures
  (password hashing, credential validation, SM-2, maturity, quiz generation,
  document extraction)

## Functional test cases

| ID | Case | Steps | Expected | Actual | Result |
|----|------|-------|----------|--------|--------|
| F1 | Sign in and session persistence | Launch the app, tap the demo autofill card, tap Sign In to Beezy, force-stop the app, relaunch | The app opens on the sign-in screen first; after sign-in the deck list appears; the session survives a relaunch | Sign-in screen shown on first launch, deck list after login, still signed in after force-stop/relaunch | Pass |
| F2 | Create a deck and persist it | Decks → New Deck → title "Persistence Test" → Create Deck, force-stop, relaunch | The deck is listed and still there after restart | Deck appeared immediately and was present after restart | Pass |
| F3 | Card CRUD, search, filter | Open a deck → Add Card with question "Card CRUD test?" and answer "Saved and searchable" → save; search "CRUD"; edit the answer; delete the card | Card count changes 3 → 4; search shows only the matching card; the edited answer is saved; delete asks for confirmation and returns the count to 3 | All steps behaved as expected; the delete confirmation reads "Delete Card?" with Cancel/Delete | Pass |
| F4 | Flashcard review and SM-2 rating | Open a deck → Flashcards → tap the card to flip → rate Good | The card flips and the rating advances to the next card | Card flipped and showed the rating bar; Good advanced to the next card; the SM-2 arithmetic is covered by unit tests | Pass |
| F5 | Quiz attempt | Open a deck → Quiz Mode → select an answer | The chosen option is marked, the score updates, and the next question loads automatically after about one second | Option B was marked correct, SCORE updated, Question 2 loaded | Pass |
| F6 | Pomodoro timer and notification | Pomodoro → Start → allow notifications | The countdown starts, a foreground notification shows the remaining time, and Pause/Reset/Skip work | Permission dialog shown, countdown ran (24:56 captured), notification "Beezy Focus Session" on `beezy_pomodoro_channel`, controls respond | Pass |
| F7 | Edit a deck | Decks → pencil icon on a deck → change the title → Save Changes, force-stop, relaunch | The dialog opens prefilled, the card updates without a relaunch, and the change survives a restart | Dialog opened with the current title, description, and tags; the card title changed immediately and was still there after relaunch | Pass |
| F8 | Rotation keeps study sessions | Open a deck → Flashcards, flip a card, note the counter; rotate to landscape and back. Then Quiz → answer two questions → rotate twice. Then type a search on the study screen and rotate | The same card stays flipped at the same counter; the quiz keeps its question and score; the search text and tag filter stay | Flashcard stayed on "Card 2 of 3", answer side, after landscape and back; quiz kept "Question 3 of 3" and "SCORE: 1" after both rotations; the study screen kept the "card" search and the Recall tag, and the filtered card was still listed | Pass |
| F9 | Timer settings persist and drive the timer | Pomodoro → Timer settings → focus 30, short 10, long 20 → Save → Reset; switch to Short break and back; force-stop and relaunch | Reset shows 30:00, each mode uses its saved length, and the values survive a restart | Save set the current view to 30:00; Short break showed 10:00 and Long break 20:00; after force-stop and relaunch Focus was still 30:00. Changing Focus to 35 while running kept the countdown at 29:12, and Reset then showed 35:00 | Pass |
| F10 | A failed write shows an error, not a crash | Debug build: make the database directory read-only (`run-as … chmod 500 databases`) since the emulator image has no `sqlite3`, then create a deck; restore the permissions and create again | A dismissible error banner appears, the app stays up, no deck is added, and the next create succeeds | The dialog showed "Could not create the deck. Please try again." inline with the typed title intact, the app stayed up, and the deck was absent; after `chmod 700` the same create succeeded | Pass |

## Security test cases

| ID | Case | Steps | Expected | Actual | Result |
|----|------|-------|----------|--------|--------|
| S1 | Passwords are not stored in plaintext | Install the debug build, sign in, pull `databases/beezy.db` with `run-as`, inspect `users` | No readable password; only a derived hash | `passwordHash` is `pbkdf2$120000$…`, 80 characters, unreadable; the demo password is nowhere in the file | Pass |
| S2 | Invalid login is rejected | Sign in with the demo username and a wrong password; then sign in with a username that does not exist | The same generic message for both, no session created, and nothing that confirms whether the account exists | Both attempts showed "Incorrect username or password." and the app stayed on the sign-in screen | Pass |
| S3 | Accounts cannot see each other's decks | As demo create a deck named "Demo Only"; log out; create a second account "tester" | The new account sees only its own starter decks | "Demo Only" was absent; both seeded decks were present | Pass |
| S4 | Logout clears the session | Sign in, open About, tap Log Out | Return to the sign-in screen with no account chip, and stay signed out after relaunch | Signed-out screen shown; account chip gone; relaunch opened sign-in | Pass |
| S5 | SQL injection text is not executed | Create a deck titled `' OR 1=1 --` | The title is stored and displayed as text; no crash and no data loss | Deck listed literally; app stayed alive; the rest of the list was unchanged | Pass |
| S6 | Backups are disabled | Inspect `AndroidManifest.xml` | `allowBackup="false"` and no backup rules | `allowBackup="false"`, no `dataExtractionRules`/`fullBackupContent` | Pass |
| S7 | No secrets or sensitive logging | Search the sources | No API keys, tokens, or credential logging | `grep` over `app/src` found no keys, and no `Log`/`println` calls exist | Pass |
| S8 | Passwords are masked while typing | Open the sign-in screen and type a password; tap the eye button | Dots by default; the eye reveals the text and taps again hide it | Field rendered as dots during typing, the eye revealed "password123", a second tap masked it again, and the icon switched | Pass |
| S9 | Only necessary permissions are requested | Inspect the merged manifest and `dumpsys package com.beezy.app` | Only the notification and foreground-service permissions used by the Pomodoro timer | `POST_NOTIFICATIONS`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`; no storage, media, or vibration permissions | Pass |
| S10 | Malformed credentials are rejected before an account exists | Create Account with a 2-character username, then with the password "short7" | Each attempt is rejected with a specific message and no account is created | "Username must be 3–20 characters using letters, numbers, dots, dashes, or underscores" then "Password must be at least 8 characters"; the app stayed on the form. The email rule is covered by `AuthValidationTest` | Pass |

## Not covered

- Process death during a flashcard or quiz session restarts it; only rotation is
  preserved.
- Real-device testing; all results are from the emulator.
- Very large decks and imported document text beyond the sample sizes.
