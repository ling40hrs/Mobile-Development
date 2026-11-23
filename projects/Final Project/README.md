# Beezy — Study App

Date: 2026-11-23

[Canvas project](https://ciit.instructure.com/courses/26695/quizzes/11648)

An Android flashcard study app in Kotlin with local SQLite storage: build decks, review cards with spaced repetition, take quizzes, and time study sessions.

---

## Features

1. **Local Accounts**: Sign up or log in; credentials and profile are stored in SQLite (seed account: `demo` / `password123`).
2. **Decks**: Create decks with a title, description, and tags; search by name or tag, filter by tag, and sort by date, title, or card count.
3. **Cards**: Add, edit, and delete flashcards in a deck; star decks and reset their progress.
4. **Spaced Repetition**: Flip a card and rate it Again, Hard, Good, or Easy; SM-2 updates its interval, ease factor, and due date.
5. **Quizzes**: Generate a multiple-choice quiz from a deck and review the result.
6. **Pomodoro Timer**: Run focus and break sessions in a foreground service with notifications.
7. **Profile & Settings**: Edit display name and avatar, switch light/dark theme (saved with DataStore), and reset local data.
8. **Sample Data**: Starter decks and cards are inserted on first launch.

---

## Project Structure

```
├── build.gradle.kts                   # plugins via version catalog
├── settings.gradle.kts
├── gradle/
│   ├── libs.versions.toml             # dependency versions
│   ├── gradle-daemon-jvm.properties
│   └── wrapper/
├── gradlew / gradlew.bat
└── app/
    ├── build.gradle.kts               # Compose, compileSdk 37, minSdk 26
    ├── proguard-rules.pro
    └── src
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/beezy/app
        │   │   ├── MainActivity.kt
        │   │   ├── BeezyApplication.kt
        │   │   ├── data/local/        # SQLiteOpenHelper, DAOs, entities, seed
        │   │   ├── data/local/datastore/   # preferences, theme mode
        │   │   ├── data/repository/   # repository implementations
        │   │   ├── domain/            # models, repository interfaces, use cases
        │   │   ├── di/                # AppContainer, dispatchers
        │   │   └── presentation/      # Compose screens, components, navigation, theme
        │   └── res/                   # drawables, mipmaps, values
        └── test/java/com/beezy/app    # JUnit tests for use cases
```

Decks, cards, and accounts live in a local `beezy.db` SQLite database behind a single `SQLiteOpenHelper`. Theme and user preferences are saved with DataStore.

---

## How to Run

### Command Line

```bash
# Windows
.\gradlew.bat test
.\gradlew.bat assembleDebug

# Linux / macOS
./gradlew test
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

### In Android Studio

1. Open the `Final Project` folder and let Gradle sync.
2. Pick an emulator or device (API 26 or higher) and press **Run**.
3. Run the tests from **Build > Run Unit Tests** or the `test` run configuration.

## Screenshots
