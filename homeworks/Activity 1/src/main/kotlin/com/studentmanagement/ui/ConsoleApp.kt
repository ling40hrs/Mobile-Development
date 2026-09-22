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

/**
 * Interactive command-line presentation controller.
 */
class ConsoleApp(
    private val repository: StudentRepository,
    private val service: EnrollmentService,
    private val validator: InputValidator = InputValidator()
) {

    fun start() {
        var isRunning = true

        while (isRunning) {
            printMainMenu()
            val choice = validator.readInt("Enter choice [0-8]: ", 0, 8)

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

        println("Grading Scale: 1.00 (Highest) down to 3.00 (Passing), 5.00 (Failing)")
        val grade = validator.readDouble("Enter Grade [1.00 - 5.00]: ", 1.00, 5.00)

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

        println("Retrieving academic record asynchronously...")
        val report = try {
            // Demonstrate asynchronous coroutine execution with suspension
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
        var sortOption = StudentSortOption.BY_ID_ASC

        var inSubmenu = true
        while (inSubmenu) {
            println()
            println("=================================")
            println("      SORT & FILTER OPTIONS      ")
            println("=================================")
            println("Active Course Filter : ${filterCourse ?: "None (All Courses)"}")
            println("Active Year Filter   : ${filterYear?.let { "Year $it" } ?: "None (All Years)"}")
            println("Active Sort Order    : ${sortOption.displayName}")
            println("---------------------------------")
            println("[1] Sort by Student ID")
            println("[2] Sort by Student Name (A-Z)")
            println("[3] Sort by Year Level (Ascending)")
            println("[4] Sort by Year Level (Descending)")
            println("[5] Filter by Course Program")
            println("[6] Filter by Year Level")
            println("[7] Reset Filters")
            println("[8] View Results Table")
            println("[0] Return to Main Menu")
            println()

            val option = validator.readInt("Select an option [0-8]: ", 0, 8)
            when (option) {
                1 -> {
                    sortOption = StudentSortOption.BY_ID_ASC
                    println("  Sort order set to Student ID.")
                }
                2 -> {
                    sortOption = StudentSortOption.BY_NAME_ASC
                    println("  Sort order set to Student Name (A-Z).")
                }
                3 -> {
                    sortOption = StudentSortOption.BY_YEAR_ASC
                    println("  Sort order set to Year Level (Ascending).")
                }
                4 -> {
                    sortOption = StudentSortOption.BY_YEAR_DESC
                    println("  Sort order set to Year Level (Descending).")
                }
                5 -> {
                    val available = Course.availableCodes().joinToString(", ")
                    val input = validator.readOptionalString("Enter course code to filter ($available) or blank to clear: ")
                    filterCourse = input?.uppercase()
                    println("  Course filter set to: ${filterCourse ?: "None"}")
                }
                6 -> {
                    val input = validator.readOptionalString("Enter year level to filter (1-4) or blank to clear: ")
                    val parsed = input?.toIntOrNull()
                    if (parsed != null && parsed in Course.VALID_YEAR_RANGE) {
                        filterYear = parsed
                        println("  Year filter set to: Year $filterYear")
                    } else if (parsed != null) {
                        println("  [!] Invalid year level. Must be between 1 and 4.")
                    } else {
                        filterYear = null
                        println("  Year filter cleared.")
                    }
                }
                7 -> {
                    filterCourse = null
                    filterYear = null
                    sortOption = StudentSortOption.BY_ID_ASC
                    println("  Filters and sorting reset to default.")
                }
                8 -> {
                    val results = repository.filterAndSort(filterCourse, filterYear, sortOption)
                    TableFormatter.printStudentList(
                        students = results,
                        title = "FILTERED & SORTED STUDENTS"
                    )
                }
                0 -> inSubmenu = false
            }
        }
    }
}
