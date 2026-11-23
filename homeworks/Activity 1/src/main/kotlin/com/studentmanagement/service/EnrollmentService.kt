package com.studentmanagement.service

import com.studentmanagement.exception.*
import com.studentmanagement.model.Course
import com.studentmanagement.model.EnrolledSubject
import com.studentmanagement.model.Student
import com.studentmanagement.model.Subject
import com.studentmanagement.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class StudentReport(
    val student: Student,
    val summary: AcademicSummary,
    val subjects: List<EnrolledSubject>,
    val generatedAtMillis: Long = System.currentTimeMillis()
)

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
        if (units <= 0 || units > 6) throw ValidationException("Subject units must be between 1 and 6.")

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

        // CIIT scale: 0.00 is a failing grade; passing grades are 1.00 to 4.00
        if (grade != 0.0 && (grade < 1.00 || grade > 4.00)) {
            throw InvalidGradeException(
                grade,
                "Grade must be 0.00 (Failed) or between 1.00 and 4.00 on the CIIT scale."
            )
        }

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

    suspend fun generateStudentReportAsync(studentId: String): StudentReport =
        withContext(Dispatchers.Default) {
            val student = repository.findById(studentId)
                ?: throw StudentNotFoundException(studentId)

            val summary = GradeCalculator.generateSummary(student.subjectList)
            StudentReport(
                student = student,
                summary = summary,
                subjects = student.subjectList.sortedBy { it.subject.code }
            )
        }
}
