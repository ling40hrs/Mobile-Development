# Student Enrollment & Grade Management System

[Canvas quiz](https://ciit.instructure.com/courses/26695/quizzes/11530)

A console student information system in Kotlin: register students, enroll subjects, record grades, and print reports.

---

## Features

1. **Register Students** (`[1]`): Add students with validation for ID, name, course program, and year level.
2. **View All Students** (`[2]`): Table of ID, name, course, year, GWA, and standing.
3. **Search Students** (`[3]`): Case-insensitive search on first name, last name, or full name.
4. **Enroll Subjects** (`[4]`): Enroll a student in a subject (code, title, units). Duplicate enrollments are blocked.
5. **Add / Update Grades** (`[5]`): CIIT AY 26-27 scale — 0.00 or 1.00 to 4.00 — with remarks (Excellent, Superior, Very Good, Good, Satisfactory, Fair, Passed, Failed).
6. **Individual Student Report** (`[6]`): Report card with total units, GWA, and standing (Excellent, Good, Failed, No Completed Subjects).
7. **Remove Student** (`[7]`): Delete a record after a yes/no confirmation.
8. **Sort / Filter Students** (`[8]`): Sort by ID, name, year, GWA, or standing; filter by course, year, or standing.
9. **Exit** (`[0]`): Quit the program.

---

## Project Structure

```
├── build.gradle.kts                   # Kotlin JVM + Coroutines
├── settings.gradle.kts
├── gradlew / gradlew.bat
└── src
    ├── main/kotlin/com/studentmanagement
    │   ├── Main.kt                    # entry point
    │   ├── model/                     # Student, Subject, Course, EnrolledSubject
    │   ├── repository/                # StudentRepository + in-memory impl with seed data
    │   ├── service/                   # GWA/standing + enrollment rules
    │   ├── ui/                        # console menu, input checks, table output
    │   └── exception/                 # app exceptions
```

---

## How to Run

### Command Line
Open a terminal in the project directory and run:

```bash
# Windows
.\gradlew.bat run --console=plain

# Linux / macOS
./gradlew run --console=plain
```

### In IntelliJ IDEA or Android Studio
1. Open the project folder and let Gradle sync.
2. Run `Main.kt` (`fun main`).
