package com.studentmanagement.data

import com.studentmanagement.R
import com.studentmanagement.model.Gender
import com.studentmanagement.model.Student
import com.studentmanagement.model.StudentData
import com.studentmanagement.model.StudentInput
import com.studentmanagement.model.Subject
import com.studentmanagement.model.YearLevel
import com.studentmanagement.validation.Validation
import com.studentmanagement.validation.Validator

object StudentRepository {

    private val students = mutableListOf<Student>()
    private var nextId = 1

    val size: Int
        get() = students.size

    fun all(): List<Student> = students.toList()

    fun findById(id: Int): Student? = students.firstOrNull { it.id == id }

    fun save(input: StudentInput, editingId: Int? = null): Validation<Student> =
        when (val validation = Validator.validateRegistration(input, all(), editingId)) {
            is Validation.Failure -> validation
            is Validation.Success -> persist(validation.value, editingId)
        }

    private fun persist(data: StudentData, editingId: Int?): Validation<Student> {
        if (editingId == null) {
            val student = Student(
                id = nextId++,
                studentNumber = data.studentNumber,
                firstName = data.firstName,
                lastName = data.lastName,
                email = data.email,
                age = data.age,
                contactNumber = data.contactNumber,
                gender = data.gender,
                yearLevel = data.yearLevel
            )
            students.add(student)
            return Validation.Success(student)
        }
        val index = students.indexOfFirst { it.id == editingId }
        if (index == -1) {
            return Validation.Failure(Validator.FIELD_STUDENT, R.string.student_not_found)
        }
        val updated = students[index].copy(
            studentNumber = data.studentNumber,
            firstName = data.firstName,
            lastName = data.lastName,
            email = data.email,
            age = data.age,
            contactNumber = data.contactNumber,
            gender = data.gender,
            yearLevel = data.yearLevel
        )
        students[index] = updated
        return Validation.Success(updated)
    }

    fun remove(id: Int): Boolean = students.removeAll { it.id == id }

    fun search(query: StudentQuery): List<Student> = students
        .filter { query.filter.matches(it) }
        .filter { it.matchesSearch(query.search) }
        .sortedWith(query.sort.comparator())

    fun totalEnrollments(): Int = students.sumOf { it.enrollments.size }

    fun overallAverage(): Double? = students
        .flatMap { it.enrollments }
        .mapNotNull { it.grade }
        .takeIf { it.isNotEmpty() }
        ?.average()

    fun clear() {
        students.clear()
        nextId = 1
    }

    fun seedIfEmpty() {
        if (students.isNotEmpty()) {
            return
        }
        registerSampleStudents()
        attachSampleGrades()
    }

    private fun registerSampleStudents() {
        val samples = listOf(
            StudentInput(
                studentNumber = "2024-0001",
                firstName = "Juan",
                lastName = "Dela Cruz",
                email = "juan.delacruz@school.edu",
                age = "18",
                contactNumber = "09171234567",
                gender = Gender.MALE,
                yearLevel = YearLevel.FIRST,
                confirmed = true
            ),
            StudentInput(
                studentNumber = "2024-0002",
                firstName = "Maria",
                lastName = "Santos",
                email = "maria.santos@school.edu",
                age = "19",
                contactNumber = "09181234568",
                gender = Gender.FEMALE,
                yearLevel = YearLevel.SECOND,
                confirmed = true
            ),
            StudentInput(
                studentNumber = "2024-0003",
                firstName = "Carlo",
                lastName = "Reyes",
                email = "carlo.reyes@school.edu",
                age = "20",
                contactNumber = "09191234569",
                gender = Gender.MALE,
                yearLevel = YearLevel.THIRD,
                confirmed = true
            ),
            StudentInput(
                studentNumber = "2024-0004",
                firstName = "Angela",
                lastName = "Cruz",
                email = "angela.cruz@school.edu",
                age = "21",
                contactNumber = "09201234570",
                gender = Gender.FEMALE,
                yearLevel = YearLevel.FOURTH,
                confirmed = true
            )
        )
        samples.forEach { save(it) }
    }

    private fun attachSampleGrades() {
        enrollStudent(1, listOf(Subject.IT101, Subject.IT102), mapOf(Subject.IT101 to 91.5, Subject.IT102 to 88.0))
        enrollStudent(2, listOf(Subject.IT202, Subject.IT301), mapOf(Subject.IT202 to 86.0, Subject.IT301 to 92.5))
        enrollStudent(3, listOf(Subject.IT103), mapOf(Subject.IT103 to 79.5))
        enrollStudent(4, listOf(Subject.IT302, Subject.IT401), mapOf(Subject.IT302 to 95.0, Subject.IT401 to 94.0))
    }

    private fun enrollStudent(id: Int, subjects: List<Subject>, grades: Map<Subject, Double>) {
        val student = findById(id) ?: return
        subjects.forEach { subject ->
            if (student.enroll(subject)) {
                grades[subject]?.let { student.setGrade(subject, it) }
            }
        }
    }
}
