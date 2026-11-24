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

## Regression checklist

Use this for the full pass before submission. Tick a box only after seeing the
expected result on the emulator; log anything else in the bug table below. The
executed cases above (F1–F10, S1–S10) are covered again here as `AU*`, `DK*`,
etc., so a full run replaces them rather than repeating them.

### Setup

- [ ] `./gradlew test assembleDebug assembleRelease` green; 27 unit tests pass
- [ ] Clean install: `adb uninstall com.beezy.app` then `./gradlew installDebug`
- [ ] Sign in as `demo` / `password123`; starter decks are the two tutorials
- [ ] Test data ready: one large deck (50 cards), one empty deck, one 1-card deck, one card with emoji
- [ ] Rotation pair ready: `settings put system accelerometer_rotation 0`, `user_rotation 1/0`; restore `accelerometer_rotation 1` at the end

### AU — Authentication

- [ ] AU1 First launch opens the sign-in screen, not the deck list
- [ ] AU2 Demo Auto-fill fills `demo` / `password123`; Sign In opens Decks
- [ ] AU3 Create Account with a valid username, email, and password signs in and seeds two decks
- [ ] AU4 Sign in with the email instead of the username works
- [ ] AU5 Wrong password and unknown username both show "Incorrect username or password."
- [ ] AU6 Duplicate username or email is rejected without creating an account
- [ ] AU7 Short username, bad email, and short password each show their own message
- [ ] AU8 Password shows dots, the eye reveals it, a second tap masks it again
- [ ] AU9 Session survives force-stop and relaunch
- [ ] AU10 Log Out returns to auth, shows no account chip, and stays signed out after relaunch

### DK — Decks

- [ ] DK1 Create with title only; dialog closes and the deck appears
- [ ] DK2 Create with description and comma-separated tags; tags render as chips
- [ ] DK3 Blank or whitespace title keeps Create disabled
- [ ] DK4 200-character title and description wrap without clipping
- [ ] DK5 Edit deck: dialog is prefilled, Save updates the card, force-stop keeps it
- [ ] DK6 Delete deck: confirmation appears; Cancel keeps it, Delete removes it and its cards
- [ ] DK7 Star toggles, sorts starred first, and survives relaunch
- [ ] DK8 Search matches title, description, and tag; no match shows the empty state
- [ ] DK9 Tag chip filters; tapping the same chip clears it
- [ ] DK10 Sort Newest, Oldest, Title (A–Z), and Most Cards reorder correctly
- [ ] DK11 Empty state CTA opens the create dialog
- [ ] DK12 Smart Notes import with `Q:`/`A:` text creates a deck with cards; malformed text creates the deck and does not crash

### CD — Cards

- [ ] CD1 Add a valid question/answer pair; count and maturity update
- [ ] CD2 Blank question or answer keeps Save disabled
- [ ] CD3 Edit a card; the new text appears immediately and after relaunch
- [ ] CD4 Delete asks for confirmation; Cancel keeps it, Delete returns the count
- [ ] CD5 Search matches question, answer, and tag
- [ ] CD6 Tag filter selects and clears
- [ ] CD7 A tag containing a comma splits on reload; no data loss
- [ ] CD8 A 50-card deck scrolls smoothly and stays responsive

### FC — Flashcards

- [ ] FC1 Tap flips front → back; tap again flips back
- [ ] FC2 Again, Hard, Good, Easy each advance to the next card
- [ ] FC3 The last card opens the session-complete dialog
- [ ] FC4 Restart session returns to card 1 and clears the complete state
- [ ] FC5 Shuffle reorders cards, resets to card 1, and highlights the icon
- [ ] FC6 Prev is disabled on card 1; Next is disabled on the last card
- [ ] FC7 Rating changes reps, interval, ease, and due date in the database
- [ ] FC8 Reset Progress asks first, then clears review data for that deck only
- [ ] FC9 Rotation keeps the card, the flipped side, and the counter

### QZ — Quiz

- [ ] QZ1 Quiz Mode opens on a deck with cards; it is disabled for an empty deck
- [ ] QZ2 A correct answer increments SCORE; a wrong one does not
- [ ] QZ3 The next question loads automatically after about one second
- [ ] QZ4 A second tap during the reveal window is ignored
- [ ] QZ5 The result dialog shows the right percentage and Retry restarts at question 1
- [ ] QZ6 A one-card deck still offers four options
- [ ] QZ7 Rotation keeps the current question and score
- [ ] QZ8 Exit returns to the Study screen with no duplicate entry in the back stack

### PM — Pomodoro

- [ ] PM1 First Start requests the notification permission
- [ ] PM2 Deny keeps the timer stopped and the app alive
- [ ] PM3 Allow starts the countdown; the ring and text tick together
- [ ] PM4 Pause and Resume work
- [ ] PM5 Reset returns to the current mode's full length
- [ ] PM6 Skip advances Focus → Short → Focus → Long after four focus sessions
- [ ] PM7 The foreground notification appears, updates, and disappears on stop
- [ ] PM8 Backgrounding the app keeps the timer running
- [ ] PM9 Screen off/on keeps the timer running
- [ ] PM10 Timer settings opens with the current 25/5/15 values
- [ ] PM11 Steppers clamp 5–90, 5–30, 5–60 in 5-minute steps; Save is disabled with no changes
- [ ] PM12 Save updates Reset and each mode, and the values survive force-stop
- [ ] PM13 Save during a run keeps the countdown; Reset afterwards uses the new length
- [ ] PM14 Force-stop during a run leaves no orphan notification

### NV — Navigation and back

- [ ] NV1 All routes open: auth, decks, study, flashcards, quiz, pomodoro, about, privacy, terms
- [ ] NV2 Bottom tabs switch without stacking duplicate screens
- [ ] NV3 Study tab opens the first deck; with no decks it returns to Decks
- [ ] NV4 Back from flashcards and quiz returns to the same deck's Study screen
- [ ] NV5 Back from Study returns to Decks
- [ ] NV6 Back on Decks exits to the launcher
- [ ] NV7 Back dismisses every dialog without leaving the screen
- [ ] NV8 Logout from About clears the whole stack; back cannot reach old screens

### AB — About and settings

- [ ] AB1 Edit Profile changes the display name; it survives relaunch
- [ ] AB2 Avatar selection updates the top bar and About header
- [ ] AB3 Light, Dark, and System themes apply immediately and persist
- [ ] AB4 Help opens and closes
- [ ] AB5 Privacy Policy and Terms of Service open and back correctly
- [ ] AB6 Reset All Data asks first; Cancel changes nothing
- [ ] AB7 Reset All Data restores the starter decks for the signed-in account only
- [ ] AB8 Delete Account asks first, removes the account, and returns to auth
- [ ] AB9 A 40-character display name truncates instead of overflowing
- [ ] AB10 With no session, About is unreachable

### DB — Persistence and data

- [ ] DB1 Decks, cards, profile, theme, and timer settings survive a full restart
- [ ] DB2 `beezy.db` is version 3 with `decks`, `cards`, and `users`
- [ ] DB3 `database/schema.sql` matches `BeezyDatabase`
- [ ] DB4 `database/beezy.db` holds only the demo account and its starter decks
- [ ] DB5 A second account sees only its own decks and cards
- [ ] DB6 Deleting a deck deletes its cards
- [ ] DB7 Deleting an account deletes its decks, cards, and user row
- [ ] DB8 Data survives a low-memory kill and relaunch

### RS — Resilience and error paths

- [ ] RS1 Forced deck insert failure shows the inline banner, no crash, no deck (F10)
- [ ] RS2 Forced card insert failure shows the banner inside Add Card and keeps the typed text
- [ ] RS3 Forced deck delete failure shows the banner and the deck stays listed
- [ ] RS4 Failed flashcard rating keeps the card and shows the banner
- [ ] RS5 Read failure at launch does not crash or lose existing data
- [ ] RS6 Process death in flashcards or quiz restarts the session cleanly (accepted)
- [ ] RS7 Rapid taps on Create, Save, and Delete never duplicate a record

### UI — Theme, layout, accessibility

- [ ] UI1 Light and dark themes use the same tokens; no raw colours or unreadable text
- [ ] UI2 Rotation keeps every screen usable, with nothing clipped by the bottom bar
- [ ] UI3 Font scale 1.3 and 2.0 keep labels readable and buttons reachable
- [ ] UI4 Keyboard (adjustResize) never covers the field being edited or the submit button
- [ ] UI5 Empty states appear for decks, cards, and search
- [ ] UI6 Icon-only buttons have content descriptions; touch targets are at least 48 dp

### SEC — Security

- [ ] SEC1 Password hashes start with `pbkdf2$120000$` and do not contain the password
- [ ] SEC2 A legacy 64-char hash account is rehashed on its next login
- [ ] SEC3 Every deck/card query takes `userId`; two accounts cannot see each other
- [ ] SEC4 `' OR 1=1 --` in deck, card, tag, search, username, and email is stored as text
- [ ] SEC5 Failed writes show fixed text only — no exception, SQL, or file path
- [ ] SEC6 `dumpsys package` lists only the three Pomodoro permissions
- [ ] SEC7 `allowBackup="false"` and no extraction rules
- [ ] SEC8 No secrets or keys in the APK or sources
- [ ] SEC9 No credential or record content in logcat
- [ ] SEC10 The database and preferences are app-private; nothing is world-readable

### Release pass

- [ ] `apksigner verify --print-certs` shows the Beezy certificate
- [ ] Uninstall the debug build, install `app-release.apk`, launch
- [ ] Smoke the release build: AU2, DK1, CD1, FC1–FC3, QZ1–QZ3, PM3, AB3, AB8
- [ ] No debug-only behaviour differs; no ANR or crash in logcat

## Bug log

| ID | Build | Steps | Expected | Actual | Severity | Status |
|----|-------|-------|----------|--------|----------|--------|
| | | | | | | |

Severity: **P1** crash, data loss, or account leak; **P2** feature broken or an
error the user cannot see; **P3** layout or copy issue.

## Exit criteria

- Every checklist item above is either ticked or logged as a P3.
- No P1 or P2 open; no crash or ANR in the logcat of the final run.
- The release build passes its smoke list.
- This file and `README.md` reflect the final build and screenshots.

## Not covered

- Process death during a flashcard or quiz session restarts it; only rotation is
  preserved.
- Real-device testing; all results are from the emulator.
- Very large decks and imported document text beyond the sample sizes.
