# Student Management Mobile App

[Canvas quiz](https://ciit.instructure.com/courses/26695/quizzes/11552)

An Android student information system in Kotlin: register students, search and filter the list, enroll subjects, and record grades.

---

## Features

1. **Register Student**: Form with validation for student number, name, email, age, contact number, gender, year level, and a confirmation checkbox.
2. **View All Students**: List of student cards showing name, number, year level, gender, enrolled subjects, and average grade.
3. **Search Students**: Case-insensitive search on first name, last name, or student number.
4. **Enroll Subjects**: Tick one or more subjects in a dialog. Subjects already taken are hidden.
5. **Add / Update Grades**: Grade from 0 to 100 for each enrolled subject.
6. **Edit Student**: Reopens the registration form filled with the current record.
7. **Remove Student**: Deletes the record after an AlertDialog confirmation.
8. **Sort / Filter Students**: Sort by name, student number, average grade, or year level; filter by year level, gender, enrollment status, or grades.
9. **Dashboard**: Student count, total enrollments, and overall average on the home screen.

---

## Project Structure

```
├── build.gradle.kts                   # plugins via version catalog
├── settings.gradle.kts
├── gradle/
│   ├── libs.versions.toml             # dependency versions
│   └── wrapper/
├── gradlew / gradlew.bat
└── app/
    ├── build.gradle.kts               # viewBinding, compileSdk 37, minSdk 24
    └── src
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/studentmanagement
        │   │   ├── model/             # Student, Subject, Gender, YearLevel, Enrollment
        │   │   ├── validation/        # field rules + error messages
        │   │   ├── data/              # repository, query, filter, sort
        │   │   ├── ui/                # Main, list, form, detail activities
        │   │   ├── ui/adapter/        # RecyclerView adapters
        │   │   ├── ui/dialog/         # sort/filter, enroll, grade dialogs
        │   │   └── util/              # extensions
        │   └── res/                   # layouts, drawables, values
        └── test/java/com/studentmanagement   # JUnit tests
```

Students are kept in a Kotlin `mutableListOf` inside `StudentRepository`. No database.

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

1. Open the `Activity 2` folder and let Gradle sync.
2. Pick an emulator or device and press **Run**.
3. Run the tests from **Build > Run Unit Tests** or the `test` run configuration.

## Screenshots
