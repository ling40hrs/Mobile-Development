package com.studentmanagement.service

import com.studentmanagement.exception.*
import com.studentmanagement.model.Course
import com.studentmanagement.model.EnrolledSubject
import com.studentmanagement.model.Student
import com.studentmanagement.model.Subject
import com.studentmanagement.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Data transfer object encapsulating a student's full academic report.
 */
data class StudentReport(
    val student: Student,
    val summary: AcademicSummary,
    val subjects: List<EnrolledSubject>,
    val generatedAtMillis: Long = System.currentTimeMillis()
)

/**
 * High-level audit metrics generated concurrently via coroutines.
 */
data class InstitutionAudit(
    val totalStudents: Int,
    val totalEnrolledSubjects: Int,
    val overallAverageGwa: Double?,
    val courseBreakdown: Map<String, Int>
)

/**
 * Business logic service coordinating student enrollment, grading, and reports.
 * Designed to be consumed by both CLI controllers and Android ViewModels.
 */
class EnrollmentService(private val repository: StudentRepository) {

    fun registerStudent(
        id: String,
        firstName: String,
        lastName: String,
        courseCode: String,
        yearLevel: Int
    ): Student {
        val trimmedId = id.trim()
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()
        val trimmedCourse = courseCode.trim().uppercase()

        if (trimmedId.isBlank()) throw ValidationException("Student ID cannot be empty.")
        if (!trimmedId.matches(Regex("^[a-zA-Z0-9-]+$"))) {
            throw ValidationException("Student ID may only contain alphanumeric characters and hyphens.")
        }
        if (trimmedFirst.isBlank()) throw ValidationException("First name cannot be empty.")
        if (trimmedLast.isBlank()) throw ValidationException("Last name cannot be empty.")
        if (yearLevel !in Course.VALID_YEAR_RANGE) {
            throw ValidationException("Year level must be between 1 and 4.")
        }

        // Validate course program: only BSCS, BSEMC, and BSIS allowed
        val courseEnum = Course.fromCode(trimmedCourse)
            ?: throw ValidationException(
                "Invalid course '$trimmedCourse'. Available courses: ${Course.availableCodes().joinToString(", ")}"
            )

        val newStudent = Student(
            id = trimmedId,
            firstName = trimmedFirst,
            lastName = trimmedLast,
            course = courseEnum.code,
            yearLevel = yearLevel
        )

        repository.insert(newStudent)
        return newStudent
    }

    fun enrollSubject(
        studentId: String,
        subjectCode: String,
        subjectTitle: String,
        units: Int
    ): EnrolledSubject {
        val student = repository.findById(studentId)
            ?: throw StudentNotFoundException(studentId)

        val codeUpper = subjectCode.trim().uppercase()
        if (codeUpper.isBlank()) throw ValidationException("Subject code cannot be blank.")
        if (subjectTitle.trim().isBlank()) throw ValidationException("Subject title cannot be blank.")
        if (units <= 0 || units > 12) throw ValidationException("Subject units must be between 1 and 12.")

        if (student.hasSubject(codeUpper)) {
            throw SubjectAlreadyEnrolledException(studentId, codeUpper)
        }

        val subject = Subject(codeUpper, subjectTitle.trim(), units)
        val enrolled = student.enrollSubject(subject)
        repository.update(student)
        return enrolled
    }

    fun recordGrade(studentId: String, subjectCode: String, grade: Double): EnrolledSubject {
        val student = repository.findById(studentId)
            ?: throw StudentNotFoundException(studentId)

        val codeUpper = subjectCode.trim().uppercase()
        val enrolled = student.getEnrolledSubject(codeUpper)
            ?: throw SubjectNotFoundException(codeUpper)

        // Validate standard grading scale: 1.00 (highest) to 5.00 (failing)
        if (grade !in 1.00..5.00) {
            throw InvalidGradeException(grade, "Grade must be on a 1.00 (highest) to 5.00 (failing) scale.")
        }

        // Round to 2 decimal places for clean precision
        val roundedGrade = Math.round(grade * 100.0) / 100.0
        enrolled.grade = roundedGrade
        repository.update(student)
        return enrolled
    }

    fun removeStudent(studentId: String): Boolean {
        if (repository.findById(studentId) == null) {
            throw StudentNotFoundException(studentId)
        }
        return repository.delete(studentId)
    }

    /**
     * Asynchronously generates an individual student academic report.
     * Demonstrates Kotlin coroutine dispatching ([Dispatchers.Default]) and non-blocking suspension.
     */
    suspend fun generateStudentReportAsync(studentId: String): StudentReport =
        withContext(Dispatchers.Default) {
            val student = repository.findById(studentId)
                ?: throw StudentNotFoundException(studentId)

            // Simulate non-blocking asynchronous report synthesis / verification
            delay(250)

            val summary = GradeCalculator.generateSummary(student.subjectList)
            StudentReport(
                student = student,
                summary = summary,
                subjects = student.subjectList.sortedBy { it.subject.code }
            )
        }

    /**
     * Demonstrates concurrent coroutine execution ([async] / [await]) across distinct metrics.
     */
    suspend fun performAuditAsync(): InstitutionAudit = coroutineScope {
        val students = repository.getAll()

        val countDeferred = async(Dispatchers.Default) {
            students.size
        }

        val subjectCountDeferred = async(Dispatchers.Default) {
            students.sumOf { it.enrolledSubjects.size }
        }

        val courseBreakdownDeferred = async(Dispatchers.Default) {
            students.groupBy { it.course }.mapValues { it.value.size }
        }

        val gwaDeferred = async(Dispatchers.Default) {
            val gwas = students.mapNotNull { GradeCalculator.calculateGWA(it.subjectList) }
            if (gwas.isNotEmpty()) gwas.average() else null
        }

        InstitutionAudit(
            totalStudents = countDeferred.await(),
            totalEnrolledSubjects = subjectCountDeferred.await(),
            overallAverageGwa = gwaDeferred.await(),
            courseBreakdown = courseBreakdownDeferred.await()
        )
    }
}
