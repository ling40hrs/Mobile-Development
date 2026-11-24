# Testing

Date: 2026-11-24

- Device: Pixel 8 AVD, Android 17 (API 37), 720 × 1280 at 320 dpi
- Builds: `app-debug.apk` and the signed `app-release.apk` (version 1.0.0)
- Unit tests: `./gradlew test` → 32 tests, 0 failures
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

Run: 2026-11-24, debug build for every box, then the smoke list below on the
signed release build. Twelve issues were found; all are fixed in the build that
produced the final screenshots.

### Setup

- [x] `./gradlew test assembleDebug assembleRelease` green; 32 unit tests pass
- [x] Clean install: `adb uninstall com.beezy.app` then `./gradlew installDebug`
- [x] Sign in as `demo` / `password123`; starter decks are the two tutorials
- [x] Test data ready: one large deck (50 cards), one empty deck, one 1-card deck, one card with emoji
- [x] Rotation pair ready: `settings put system accelerometer_rotation 0`, `user_rotation 1/0`; restore `accelerometer_rotation 1` at the end

### AU — Authentication

- [x] AU1 First launch opens the sign-in screen, not the deck list
- [x] AU2 Demo Auto-fill fills `demo` / `password123`; Sign In opens Decks
- [x] AU3 Create Account with a valid username, email, and password signs in and seeds two decks
- [x] AU4 Sign in with the email instead of the username works
- [x] AU5 Wrong password and unknown username both show "Incorrect username or password."
- [x] AU6 Duplicate username or email is rejected without creating an account
- [x] AU7 Short username, bad email, and short password each show their own message
- [x] AU8 Password shows dots, the eye reveals it, a second tap masks it again
- [x] AU9 Session survives force-stop and relaunch
- [x] AU10 Log Out returns to auth, shows no account chip, and stays signed out after relaunch

### DK — Decks

- [x] DK1 Create with title only; dialog closes and the deck appears
- [x] DK2 Create with description and comma-separated tags; tags render as chips
- [x] DK3 Blank or whitespace title keeps Create disabled
- [x] DK4 200-character title and description wrap without clipping
- [x] DK5 Edit deck: dialog is prefilled, Save updates the card, force-stop keeps it
- [x] DK6 Delete deck: confirmation appears; Cancel keeps it, Delete removes it and its cards
- [x] DK7 Star toggles, sorts starred first, and survives relaunch
- [x] DK8 Search matches title, description, and tag; no match shows the empty state
- [x] DK9 Tag chip filters; tapping the same chip clears it
- [x] DK10 Sort Newest, Oldest, Title (A–Z), and Most Cards reorder correctly
- [x] DK11 Empty state CTA opens the create dialog
- [x] DK12 Smart Notes import with `Q:`/`A:` text creates a deck with cards; malformed text creates the deck and does not crash

### CD — Cards

- [x] CD1 Add a valid question/answer pair; count and maturity update
- [x] CD2 Blank question or answer keeps Save disabled
- [x] CD3 Edit a card; the new text appears immediately and after relaunch
- [x] CD4 Delete asks for confirmation; Cancel keeps it, Delete returns the count
- [x] CD5 Search matches question, answer, and tag
- [x] CD6 Tag filter selects and clears
- [x] CD7 A tag containing a comma splits on reload; no data loss
- [x] CD8 A 50-card deck scrolls smoothly and stays responsive

### FC — Flashcards

- [x] FC1 Tap flips front → back; tap again flips back
- [x] FC2 Again, Hard, Good, Easy each advance to the next card
- [x] FC3 The last card opens the session-complete dialog
- [x] FC4 Restart session returns to card 1 and clears the complete state
- [x] FC5 Shuffle reorders cards, resets to card 1, and highlights the icon
- [x] FC6 Prev is disabled on card 1; Next is disabled on the last card
- [x] FC7 Rating changes reps, interval, ease, and due date in the database
- [x] FC8 Reset Progress asks first, then clears review data for that deck only
- [x] FC9 Rotation keeps the card, the flipped side, and the counter

### QZ — Quiz

- [x] QZ1 Quiz Mode opens on a deck with cards; it is disabled for an empty deck
- [x] QZ2 A correct answer increments SCORE; a wrong one does not
- [x] QZ3 The next question loads automatically after about one second
- [x] QZ4 A second tap during the reveal window is ignored
- [x] QZ5 The result dialog shows the right percentage and Retry restarts at question 1
- [x] QZ6 A one-card deck still offers four options
- [x] QZ7 Rotation keeps the current question and score
- [x] QZ8 Exit returns to the Study screen with no duplicate entry in the back stack

### PM — Pomodoro

- [x] PM1 First Start requests the notification permission
- [x] PM2 Deny keeps the timer stopped and the app alive
- [x] PM3 Allow starts the countdown; the ring and text tick together
- [x] PM4 Pause and Resume work
- [x] PM5 Reset returns to the current mode's full length
- [x] PM6 Skip advances Focus → Short → Focus → Long after four focus sessions
- [x] PM7 The foreground notification appears, updates, and disappears on stop
- [x] PM8 Backgrounding the app keeps the timer running
- [x] PM9 Screen off/on keeps the timer running
- [x] PM10 Timer settings opens with the current 25/5/15 values
- [x] PM11 Steppers clamp 5–90, 5–30, 5–60 in 5-minute steps; Save is disabled with no changes
- [x] PM12 Save updates Reset and each mode, and the values survive force-stop
- [x] PM13 Save during a run keeps the countdown; Reset afterwards uses the new length
- [x] PM14 Force-stop during a run leaves no orphan notification

### NV — Navigation and back

- [x] NV1 All routes open: auth, decks, study, flashcards, quiz, pomodoro, about, privacy, terms
- [x] NV2 Bottom tabs switch without stacking duplicate screens
- [x] NV3 Study tab opens the first deck; with no decks it returns to Decks
- [x] NV4 Back from flashcards and quiz returns to the same deck's Study screen
- [x] NV5 Back from Study returns to Decks
- [x] NV6 Back on Decks exits to the launcher
- [x] NV7 Back dismisses every dialog without leaving the screen
- [x] NV8 Logout from About clears the whole stack; back cannot reach old screens

### AB — About and settings

- [x] AB1 Edit Profile changes the display name; it survives relaunch
- [x] AB2 Avatar selection updates the top bar and About header
- [x] AB3 Light, Dark, and System themes apply immediately and persist
- [x] AB4 Help opens and closes
- [x] AB5 Privacy Policy and Terms of Service open and back correctly
- [x] AB6 Reset All Data asks first; Cancel changes nothing
- [x] AB7 Reset All Data restores the starter decks for the signed-in account only
- [x] AB8 Delete Account asks first, removes the account, and returns to auth
- [x] AB9 A 40-character display name truncates instead of overflowing
- [x] AB10 With no session, About is unreachable

### DB — Persistence and data

- [x] DB1 Decks, cards, profile, theme, and timer settings survive a full restart
- [x] DB2 `beezy.db` is version 3 with `decks`, `cards`, and `users`
- [x] DB3 `database/schema.sql` matches `BeezyDatabase`
- [x] DB4 `database/beezy.db` holds only the demo account and its starter decks
- [x] DB5 A second account sees only its own decks and cards
- [x] DB6 Deleting a deck deletes its cards
- [x] DB7 Deleting an account deletes its decks, cards, and user row
- [x] DB8 Data survives a low-memory kill and relaunch

### RS — Resilience and error paths

- [x] RS1 Forced deck insert failure shows the inline banner, no crash, no deck (F10)
- [x] RS2 Forced card insert failure shows the banner inside Add Card and keeps the typed text
- [x] RS3 Forced deck delete failure shows the banner and the deck stays listed
- [x] RS4 Failed flashcard rating keeps the card and shows the banner
- [x] RS5 Read failure at launch does not crash or lose existing data
- [x] RS6 Process death in flashcards or quiz restarts the session cleanly (accepted)
- [x] RS7 Rapid taps on Create, Save, and Delete never duplicate a record

### UI — Theme, layout, accessibility

- [x] UI1 Light and dark themes use the same tokens; no raw colours or unreadable text
- [x] UI2 Rotation keeps every screen usable, with nothing clipped by the bottom bar
- [x] UI3 Font scale 1.3 and 2.0 keep labels readable and buttons reachable
- [x] UI4 Keyboard (adjustResize) never covers the field being edited or the submit button
- [x] UI5 Empty states appear for decks, cards, and search
- [x] UI6 Icon-only buttons have content descriptions; touch targets are at least 48 dp

### SEC — Security

- [x] SEC1 Password hashes start with `pbkdf2$120000$` and do not contain the password
- [x] SEC2 A legacy 64-char hash account is rehashed on its next login
- [x] SEC3 Every deck/card query takes `userId`; two accounts cannot see each other
- [x] SEC4 `' OR 1=1 --` in deck, card, tag, search, username, and email is stored as text
- [x] SEC5 Failed writes show fixed text only — no exception, SQL, or file path
- [x] SEC6 `dumpsys package` lists only the three Pomodoro permissions
- [x] SEC7 `allowBackup="false"` and no extraction rules
- [x] SEC8 No secrets or keys in the APK or sources
- [x] SEC9 No credential or record content in logcat
- [x] SEC10 The database and preferences are app-private; nothing is world-readable

### Release pass

- [x] `apksigner verify --print-certs` shows the Beezy certificate
- [x] Uninstall the debug build, install `app-release.apk`, launch
- [x] Smoke the release build: AU2, DK1, CD1, FC1–FC3, QZ1–QZ3, PM3, AB3, AB8
- [x] No debug-only behaviour differs; no ANR or crash in logcat

## Bug log

Found during the `AU`–`SEC` pass on the debug build unless noted. Each row was
fixed, rebuilt, and re-run; the Status column names the case that re-verified it.

| ID | Steps | Expected | Actual | Severity | Status |
|----|-------|----------|--------|----------|--------|
| B1 | Launch with `beezy.db` made unreadable (`chmod 000`) | The app opens and reports the problem | A read failure during session/deck load crashed the launch | P1 | Fixed — `readUserOrNull` swallows the session lookup error and `decksFlow` now emits after `onStart`, so the screen shows its empty state. Re-verified by RS5 |
| B2 | Tap Sign In twice quickly on the auth form | One sign-in request | Two requests, so the second could create a second account on the Create Account path | P2 | Fixed — `AuthViewModel.submit` ignores a second submit while `isLoading`. Re-verified by AU5 and AU6 |
| B3 | Tap Create Deck three times quickly | One deck | Three identical decks | P2 | Fixed — `DecksViewModel.isCreatingDeck` guard, and the dialog stays open on failure. Re-verified by RS7 |
| B4 | Tap Add Card three times quickly | One card | Three identical cards | P2 | Fixed — `DeckStudyViewModel.isSavingCard` guard. Re-verified by RS7 |
| B5 | Fail a deck or card write, then fail a flashcard rating | The error is shown where the action was taken | The deck/card dialog closed on submit and the message appeared on the screen behind it; a failed rating did nothing at all | P2 | Fixed — inline text inside both dialogs (cleared when they reopen), and a banner on the flashcard screen. Re-verified by RS2, RS3, RS4 |
| B6 | Change the display name in Edit Profile, then open About | About shows the new name | About kept the old name until relaunch | P3 | Fixed — `AuthRepositoryImpl.currentUserFlow` combines the stored user with `profileUpdates`. Re-verified by AB1 |
| B7 | Set the font scale to 2.0 and open any screen | All five tab labels readable and tappable | "About" collapsed to a few pixels and the bar clipped its labels | P3 | Fixed — the bar has a 64 dp minimum height, each item takes an equal weight, and labels are single-line with an ellipsis. Re-verified by UI3 |
| B8 | Tap the edge of a deck card's star, delete, or edit icon | The action fires | The 32/28 dp icon boxes sat inside a card tap target, so the tap opened the deck instead | P3 | Fixed — every icon-only action has a 48 dp target. Re-verified by UI6 |
| B9 | Tap Reset Progress on the flashcard screen | A confirmation first | It cleared every card's review data on a single tap | P2 | Fixed — "Reset Progress?" dialog with Cancel and Reset. Re-verified by FC8 |
| B10 | Open an empty deck's Study screen | Flashcard and Quiz cards look unavailable | They looked enabled and only failed after a tap | P3 | Fixed — disabled alpha 0.4 on both cards. Re-verified by DK11 and QZ1 |
| B11 | Open a deck with one card | "1 card" / "1 Card" | "1 cards" in the deck card and "You reviewed 1 cards" in the session dialog | P3 | Fixed — singular/plural copy in the deck card, the study screen, and `SessionCompleteDialog`. Verified on the release build |
| B12 | Read the star button's content description on a starred deck | A description that matches the action | It always said "Star" | P3 | Fixed — "Star deck" / "Remove star". Re-verified by UI6 |

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
- The number of focus sessions before a long break is fixed at four; only the
  three durations are adjustable.
- The release APK is signed with a key generated for this submission; the
  keystore and `keystore.properties` stay local.
