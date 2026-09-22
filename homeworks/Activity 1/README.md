# Student Enrollment & Grade Management System

[Canvas quiz](https://ciit.instructure.com/courses/26695/quizzes/11530)

A console-based student information, subject enrollment, and grade management system written in Kotlin.

This project is structured using clean architectural patterns (Model-Repository-Service-UI) with Kotlin Coroutines for asynchronous operations, making it easy to understand and directly transition into an Android application (e.g., using Room + ViewModel + Coroutines).

---

## Features

1. **Register Students** (`[1]`): Add students with validation for ID, name, course program, and year level.
2. **View All Students** (`[2]`): Formatted tabular view displaying ID, Name, Course, and Year.
3. **Search Students** (`[3]`): Case-insensitive multi-field search (first name, last name, or full name).
4. **Enroll Subjects** (`[4]`): Enroll students into subjects with code, title, and unit credits (prevents duplicate subject enrollments).
5. **Add / Update Grades** (`[5]`): Assign or update grades on the standard collegiate scale (1.00 to 5.00) with automatic remarks (Excellent, Very Good, Good, Passed, Failed).
6. **Individual Student Report** (`[6]`): Asynchronously generated academic report card calculating Total Units, General Weighted Average (GWA), and Academic Standing (President's List, Dean's List, Good Standing, Warning).
7. **Remove Student** (`[7]`): Safe deletion with confirmation prompt.
8. **Sort / Filter Students** (`[8]`): Multi-criteria sorting (by ID, Name A-Z, Year Asc/Desc) and filtering (by Course, by Year Level).
9. **Clean Exit** (`[0]`): Graceful termination.

---

## Project Structure

```
├── build.gradle.kts                   # Gradle configuration with Kotlin JVM & Coroutines
├── settings.gradle.kts                # Project definition
├── gradlew / gradlew.bat              # Gradle wrapper
└── src
    ├── main/kotlin/com/studentmanagement
    │   ├── Main.kt                    # Application entry point
    │   ├── model/
    │   │   ├── Course.kt              # Degree programs & year levels
    │   │   ├── Subject.kt             # Subject definition (code, title, units)
    │   │   ├── EnrolledSubject.kt     # Enrollment record with grade & remarks
    │   │   └── Student.kt             # Student entity with enrolled subjects
    │   ├── repository/
    │   │   ├── StudentRepository.kt   # Repository interface
    │   │   └── InMemoryStudentRepository.kt # In-memory implementation with seed data
    │   ├── service/
    │   │   ├── GradeCalculator.kt     # GWA calculation & honors evaluation
    │   │   └── EnrollmentService.kt   # Business rules & coroutine async pipelines
    │   ├── ui/
    │   │   ├── ConsoleApp.kt          # Menu navigation & user workflows
    │   │   ├── InputValidator.kt      # Safe console input parsing & bounds checking
    │   │   └── TableFormatter.kt      # Formatted tabular and report card layouts
    │   └── exception/
    │       └── Exceptions.kt          # Domain exception hierarchy
    └── test/kotlin/com/studentmanagement
        ├── StudentRepositoryTest.kt   # Repository CRUD, search, and sorting tests
        ├── GradeCalculatorTest.kt     # GWA computation & honors benchmark tests
        └── EnrollmentServiceTest.kt   # Registration, grading, and coroutine tests
```

---

## How to Run

### Command Line
Open a terminal in the project directory and run:

```bash
# On Windows (PowerShell or Command Prompt):
.\gradlew.bat run --console=plain

# On Linux / macOS:
./gradlew run --console=plain
```

### In Android Studio or IntelliJ IDEA
1. Open Android Studio or IntelliJ IDEA.
2. Select **File -> Open...** and select this directory (`Mobile Dev (kotlin) projects`).
3. Allow Gradle to sync.
4. Open `src/main/kotlin/com/studentmanagement/Main.kt` and click the green **Run** arrow next to `fun main()`.

---

## Running Automated Tests

```bash
.\gradlew.bat test
```
