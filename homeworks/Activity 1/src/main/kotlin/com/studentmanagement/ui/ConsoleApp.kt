package com.studentmanagement.ui

import com.studentmanagement.exception.DuplicateStudentException
import com.studentmanagement.exception.StudentNotFoundException
import com.studentmanagement.exception.SubjectAlreadyEnrolledException
import com.studentmanagement.exception.SubjectNotFoundException
import com.studentmanagement.exception.ValidationException
import com.studentmanagement.model.Course
import com.studentmanagement.repository.StudentRepository
import com.studentmanagement.repository.StudentSortOption
import com.studentmanagement.service.EnrollmentService
import kotlinx.coroutines.runBlocking

class ConsoleApp(
    private val repository: StudentRepository,
    private val service: EnrollmentService,
    private val validator: InputValidator = InputValidator()
) {

    fun start() {
        var isRunning = true

        while (isRunning) {
            printMainMenu()
            val choice = try {
                validator.readInt("Enter choice [0-8]: ", 0, 8)
            } catch (e: NoSuchElementException) {
                println("Input stream closed. Exiting...")
                break
            }

            try {
                when (choice) {
                    1 -> registerStudentWorkflow()
                    2 -> viewAllStudentsWorkflow()
                    3 -> searchStudentWorkflow()
                    4 -> enrollSubjectWorkflow()
                    5 -> addOrUpdateGradeWorkflow()
                    6 -> viewStudentReportWorkflow()
                    7 -> removeStudentWorkflow()
                    8 -> sortFilterWorkflow()
                    0 -> {
                        println()
                        println("Thank you for using the Student Enrollment & Grade Management System. Goodbye!")
                        isRunning = false
                    }
                }
            } catch (e: NoSuchElementException) {
                println()
                println("  Input stream closed. Exiting...")
                isRunning = false
            } catch (e: Exception) {
                println()
                println("  [ERROR] An unexpected error occurred: ${e.message}")
                println()
            }

            if (isRunning) {
                validator.pressEnterToContinue()
            }
        }
    }

    private fun printMainMenu() {
        println()
        println("========================================")
        println("    STUDENT MANAGEMENT SYSTEM          ")
        println("========================================")
        println()
        println("[1] Register Student")
        println("[2] View All Students")
        println("[3] Search Student")
        println("[4] Enroll Subject")
        println("[5] Add / Update Grade")
        println("[6] View Student Report")
        println("[7] Remove Student")
        println("[8] Sort / Filter Students")
        println("[0] Exit")
        println()
    }

    private fun registerStudentWorkflow() {
        println()
        println("--- REGISTER NEW STUDENT ---")
        val id = validator.readNonEmptyString("Enter Student ID (e.g. 1004): ")

        if (repository.findById(id) != null) {
            println("  [!] Error: A student with ID '$id' is already registered.")
            return
        }

        val firstName = validator.readNonEmptyString("Enter First Name: ")
        val lastName = validator.readNonEmptyString("Enter Last Name: ")

        val availableCourses = Course.availableCodes().joinToString("/")
        println("Available courses: $availableCourses")
        var course: String
        while (true) {
            val input = validator.readNonEmptyString("Enter Course: ").uppercase()
            if (Course.isValidCourse(input)) {
                course = input
                break
            }
            println("  [!] Invalid course '$input'. Available courses: $availableCourses")
        }
        val yearLevel = validator.readInt("Enter Year Level [1-4]: ", 1, 4)

        try {
            val student = service.registerStudent(id, firstName, lastName, course, yearLevel)
            println()
            println("  [SUCCESS] Student '${student.fullName}' (ID: ${student.id}) registered successfully!")
        } catch (e: ValidationException) {
            println("  [!] Validation Error: ${e.message}")
        } catch (e: DuplicateStudentException) {
            println("  [!] Error: ${e.message}")
        }
    }

    private fun viewAllStudentsWorkflow() {
        val students = repository.getAll()
        TableFormatter.printStudentList(students)
    }

    private fun searchStudentWorkflow() {
        println()
        println("--- SEARCH STUDENT ---")
        val query = validator.readNonEmptyString("Enter student name (Lastname or Firstname): ")
        val results = repository.searchByName(query)

        TableFormatter.printStudentList(
            students = results,
            title = "SEARCH RESULTS FOR: \"$query\""
        )
    }

    private fun enrollSubjectWorkflow() {
        println()
        println("--- ENROLL SUBJECT ---")
        val studentId = validator.readNonEmptyString("Enter Student ID: ")
        val student = repository.findById(studentId)

        if (student == null) {
            println("  [!] Error: Student with ID '$studentId' not found.")
            return
        }

        println("Enrolling for: ${student.fullName} (${student.course} - Year ${student.yearLevel})")
        if (student.enrolledSubjects.isNotEmpty()) {
            println("Currently enrolled: ${student.enrolledSubjects.keys.joinToString(", ")}")
        }

        val code = validator.readNonEmptyString("Enter Subject Code (e.g. CS103): ")
        val title = validator.readNonEmptyString("Enter Subject Title: ")
        val units = validator.readInt("Enter Subject Units [1-6]: ", 1, 6)

        try {
            val enrolled = service.enrollSubject(studentId, code, title, units)
            println()
            println("  [SUCCESS] Successfully enrolled in ${enrolled.subject.code} (${enrolled.subject.title}, ${enrolled.subject.units} units)!")
        } catch (e: SubjectAlreadyEnrolledException) {
            println("  [!] Error: ${e.message}")
        } catch (e: ValidationException) {
            println("  [!] Validation Error: ${e.message}")
        }
    }

    private fun addOrUpdateGradeWorkflow() {
        println()
        println("--- ADD / UPDATE GRADE ---")
        val studentId = validator.readNonEmptyString("Enter Student ID: ")
        val student = repository.findById(studentId)

        if (student == null) {
            println("  [!] Error: Student with ID '$studentId' not found.")
            return
        }

        if (student.enrolledSubjects.isEmpty()) {
            println("  [!] Student '${student.fullName}' is not enrolled in any subjects yet.")
            println("      Please enroll subjects first before assigning grades.")
            return
        }

        println()
        println("Enrolled Subjects for ${student.fullName}:")
        student.enrolledSubjects.values.forEach { enrolled ->
            val gradeStr = enrolled.grade?.let { "%.2f".format(it) } ?: "No Grade"
            println("  - [${enrolled.subject.code}] ${enrolled.subject.title} (${enrolled.subject.units} units) -> Current: $gradeStr (${enrolled.remarks})")
        }
        println()

        val code = validator.readNonEmptyString("Enter Subject Code to grade: ")
        if (!student.hasSubject(code)) {
            println("  [!] Error: Student is not enrolled in subject '${code.uppercase()}'.")
            return
        }

        println("CIIT AY 26-27 Grading Scale: 4.00 (Excellent) down to 1.00 (Passed), 0.00 (Failed)")
        val grade = validator.readGrade("Enter Grade [0.00 or 1.00 - 4.00]: ")

        try {
            val updated = service.recordGrade(studentId, code, grade)
            println()
            println("  [SUCCESS] Grade for ${updated.subject.code} updated to %.2f!".format(updated.grade))
            println("  Status: ${updated.status.label} (${updated.remarks})")
        } catch (e: Exception) {
            println("  [!] Error: ${e.message}")
        }
    }

    private fun viewStudentReportWorkflow() {
        println()
        println("--- VIEW INDIVIDUAL STUDENT REPORT ---")
        val studentId = validator.readNonEmptyString("Enter Student ID: ")

        val report = try {
            runBlocking {
                service.generateStudentReportAsync(studentId)
            }
        } catch (e: StudentNotFoundException) {
            println("  [!] Error: ${e.message}")
            return
        }

        TableFormatter.printStudentReport(report)
    }

    private fun removeStudentWorkflow() {
        println()
        println("--- REMOVE STUDENT ---")
        val studentId = validator.readNonEmptyString("Enter Student ID to remove: ")
        val student = repository.findById(studentId)

        if (student == null) {
            println("  [!] Error: Student with ID '$studentId' not found.")
            return
        }

        println("Found: ${student.fullName} | ${student.course} | Year ${student.yearLevel} | ${student.enrolledSubjects.size} subjects")
        val confirmed = validator.readConfirmation("Are you sure you want to permanently delete this student record?")

        if (confirmed) {
            service.removeStudent(studentId)
            println("  [SUCCESS] Student '$studentId' (${student.fullName}) has been removed.")
        } else {
            println("  Action cancelled. Student record preserved.")
        }
    }

    private fun sortFilterWorkflow() {
        var filterCourse: String? = null
        var filterYear: Int? = null
        var filterStanding: String? = null
        var sortOption = StudentSortOption.BY_ID_ASC

        var inSubmenu = true
        while (inSubmenu) {
            val results = repository.filterAndSort(filterCourse, filterYear, filterStanding, sortOption)

            val filterDesc = buildList {
                add("Course: ${filterCourse ?: "ALL"}")
                add("Year: ${filterYear?.let { "Year $it" } ?: "ALL"}")
                add("Standing: ${filterStanding ?: "ALL"}")
            }.joinToString(" | ")

            TableFormatter.printStudentList(
                students = results,
                title = "STUDENT DIRECTORY ($filterDesc | Sorted by: ${sortOption.displayName})"
            )

            println("--- SORT & FILTER MENU ---")
            println("[1] Change Sort Order")
            println("[2] Filter by Course Program")
            println("[3] Filter by Year Level")
            println("[4] Filter by Academic Standing (Excellent, Good, Failed)")
            println("[5] Reset All Filters & Sorting")
            println("[0] Return to Main Menu")
            println()

            val option = validator.readInt("Select an option [0-5]: ", 0, 5)
            when (option) {
                1 -> {
                    println()
                    println("--- SELECT SORT STRATEGY ---")
                    println("[1] Student ID (Ascending)")
                    println("[2] Student ID (Descending)")
                    println("[3] Student Name (A-Z)")
                    println("[4] Student Name (Z-A)")
                    println("[5] Year Level (1 to 4)")
                    println("[6] Year Level (4 to 1)")
                    println("[7] GWA / GPA (Highest to Lowest)")
                    println("[8] GWA / GPA (Lowest to Highest)")
                    println("[9] Academic Standing (Excellent -> Good -> Failed)")
                    val sortChoice = validator.readInt("Enter choice [1-9]: ", 1, 9)
                    sortOption = when (sortChoice) {
                        1 -> StudentSortOption.BY_ID_ASC
                        2 -> StudentSortOption.BY_ID_DESC
                        3 -> StudentSortOption.BY_NAME_ASC
                        4 -> StudentSortOption.BY_NAME_DESC
                        5 -> StudentSortOption.BY_YEAR_ASC
                        6 -> StudentSortOption.BY_YEAR_DESC
                        7 -> StudentSortOption.BY_GWA_DESC
                        8 -> StudentSortOption.BY_GWA_ASC
                        9 -> StudentSortOption.BY_STANDING
                        else -> sortOption
                    }
                    println("  [OK] Sort order set to: ${sortOption.displayName}")
                }
                2 -> {
                    println()
                    println("--- FILTER BY COURSE ---")
                    val available = Course.availableCodes().joinToString(", ")
                    println("Available courses: $available")
                    val input = validator.readOptionalString("Enter course code to filter ($available) or blank to clear: ")
                    if (input.isNullOrBlank()) {
                        filterCourse = null
                        println("  [OK] Course filter cleared.")
                    } else {
                        val upper = input.trim().uppercase()
                        if (Course.isValidCourse(upper)) {
                            filterCourse = upper
                            println("  [OK] Course filter set to: $filterCourse")
                        } else {
                            println("  [!] Invalid course '$input'. Available courses: $available")
                        }
                    }
                }
                3 -> {
                    println()
                    println("--- FILTER BY YEAR LEVEL ---")
                    val input = validator.readOptionalString("Enter year level to filter (1-4) or blank to clear: ")
                    if (input.isNullOrBlank()) {
                        filterYear = null
                        println("  [OK] Year filter cleared.")
                    } else {
                        val parsed = input.trim().toIntOrNull()
                        if (parsed != null && parsed in Course.VALID_YEAR_RANGE) {
                            filterYear = parsed
                            println("  [OK] Year filter set to: Year $filterYear")
                        } else {
                            println("  [!] Invalid year level. Must be between 1 and 4.")
                        }
                    }
                }
                4 -> {
                    println()
                    println("--- FILTER BY ACADEMIC STANDING ---")
                    println("[1] Excellent")
                    println("[2] Good")
                    println("[3] Failed")
                    println("[4] Clear Standing Filter")
                    val standChoice = validator.readInt("Select standing [1-4]: ", 1, 4)
                    when (standChoice) {
                        1 -> {
                            filterStanding = "Excellent"
                            println("  [OK] Filter set to: Excellent")
                        }
                        2 -> {
                            filterStanding = "Good"
                            println("  [OK] Filter set to: Good")
                        }
                        3 -> {
                            filterStanding = "Failed"
                            println("  [OK] Filter set to: Failed")
                        }
                        4 -> {
                            filterStanding = null
                            println("  [OK] Standing filter cleared.")
                        }
                    }
                }
                5 -> {
                    filterCourse = null
                    filterYear = null
                    filterStanding = null
                    sortOption = StudentSortOption.BY_ID_ASC
                    println("  [OK] All filters and sorting reset to default.")
                }
                0 -> inSubmenu = false
            }
        }
    }
}
