package com.studentmanagement.repository

import com.studentmanagement.exception.DuplicateStudentException
import com.studentmanagement.model.EnrolledSubject
import com.studentmanagement.model.Student
import com.studentmanagement.model.Subject
import java.util.concurrent.ConcurrentHashMap

/**
 * Stores students in memory. Loads sample records on startup unless seeding is turned off.
 */
class InMemoryStudentRepository(seedInitialData: Boolean = true) : StudentRepository {

    private val storage = ConcurrentHashMap<String, Student>()

    init {
        if (seedInitialData) {
            seedSampleData()
        }
    }

    override fun getAll(): List<Student> {
        return storage.values.sortedWith(compareBy({ it.id.toLongOrNull() ?: Long.MAX_VALUE }, { it.id }))
    }

    override fun findById(id: String): Student? {
        return storage[id.trim()]
    }

    override fun searchByName(query: String): List<Student> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return getAll()

        return storage.values.filter { student ->
            student.lastName.lowercase().contains(trimmed) ||
                    student.firstName.lowercase().contains(trimmed) ||
                    student.fullName.lowercase().contains(trimmed)
        }.sortedBy { it.lastName }
    }

    override fun insert(student: Student) {
        val key = student.id.trim()
        if (storage.containsKey(key)) {
            throw DuplicateStudentException(key)
        }
        storage[key] = student
    }

    override fun update(student: Student) {
        storage[student.id.trim()] = student
    }

    override fun delete(id: String): Boolean {
        return storage.remove(id.trim()) != null
    }

    override fun filterAndSort(
        filterCourse: String?,
        filterYear: Int?,
        filterStanding: String?,
        sortBy: StudentSortOption
    ): List<Student> {
        return storage.values
            .asSequence()
            .filter { student ->
                filterCourse.isNullOrBlank() || student.course.equals(filterCourse.trim(), ignoreCase = true)
            }
            .filter { student ->
                filterYear == null || student.yearLevel == filterYear
            }
            .filter { student ->
                if (filterStanding.isNullOrBlank()) true
                else {
                    student.academicStanding.contains(filterStanding.trim(), ignoreCase = true)
                }
            }
            .toList()
            .let { list ->
                when (sortBy) {
                    StudentSortOption.BY_ID_ASC -> list.sortedWith(idComparator())
                    StudentSortOption.BY_ID_DESC -> list.sortedWith(idComparator().reversed())
                    StudentSortOption.BY_NAME_ASC -> list.sortedWith(
                        compareBy({ it.lastName.lowercase() }, { it.firstName.lowercase() })
                    )
                    StudentSortOption.BY_NAME_DESC -> list.sortedWith(
                        compareByDescending<Student> { it.lastName.lowercase() }
                            .thenByDescending { it.firstName.lowercase() }
                    )
                    StudentSortOption.BY_YEAR_ASC -> list.sortedWith(
                        compareBy({ it.yearLevel }, { it.id })
                    )
                    StudentSortOption.BY_YEAR_DESC -> list.sortedWith(
                        compareByDescending<Student> { it.yearLevel }.thenBy { it.id }
                    )
                    StudentSortOption.BY_GWA_DESC -> list.sortedWith(
                        compareByDescending<Student> { it.gwa ?: -1.0 }.thenBy { it.id }
                    )
                    StudentSortOption.BY_GWA_ASC -> list.sortedWith(
                        compareBy<Student> { it.gwa ?: 999.0 }.thenBy { it.id }
                    )
                    StudentSortOption.BY_STANDING -> list.sortedWith(
                        compareBy<Student> {
                            when {
                                it.academicStanding.contains("Excellent", ignoreCase = true) -> 1
                                it.academicStanding.contains("Good", ignoreCase = true) -> 2
                                it.academicStanding.contains("Failed", ignoreCase = true) -> 3
                                else -> 4
                            }
                        }.thenByDescending { it.gwa ?: 0.0 }
                    )
                }
            }
    }

    private fun idComparator(): Comparator<Student> =
        compareBy({ it.id.toLongOrNull() ?: Long.MAX_VALUE }, { it.id })

    private fun seedSampleData() {
        val juan = Student("1001", "Juan", "Dela Cruz", "BSIS", 2)
        juan.enrolledSubjects["IS101"] = EnrolledSubject(Subject("IS101", "Fundamentals of Info Systems", 3), 2.50)
        juan.enrolledSubjects["IS102"] = EnrolledSubject(Subject("IS102", "Business Process Management", 3), 2.75)
        juan.enrolledSubjects["IS103"] = EnrolledSubject(Subject("IS103", "Enterprise Architecture", 3), 3.00)
        storage[juan.id] = juan

        val maria = Student("1002", "Maria", "Santos", "BSCS", 1)
        maria.enrolledSubjects["CS101"] = EnrolledSubject(Subject("CS101", "Discrete Mathematics", 3), 3.75)
        maria.enrolledSubjects["CS102"] = EnrolledSubject(Subject("CS102", "Object-Oriented Programming", 3), 4.00)
        storage[maria.id] = maria

        val pedro = Student("1003", "Pedro", "Reyes", "BSEMC", 3)
        pedro.enrolledSubjects["EMC201"] = EnrolledSubject(Subject("EMC201", "Game Design Principles", 3), 2.00)
        pedro.enrolledSubjects["EMC202"] = EnrolledSubject(Subject("EMC202", "Digital 2D Animation", 3), 0.00)
        pedro.enrolledSubjects["EMC203"] = EnrolledSubject(Subject("EMC203", "Mobile Game Development", 3), null)
        storage[pedro.id] = pedro
    }
}
