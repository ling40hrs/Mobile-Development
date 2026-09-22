package com.studentmanagement.repository

import com.studentmanagement.model.Student

/**
 * Supported sorting strategies for student listings.
 */
enum class StudentSortOption(val displayName: String) {
    BY_ID_ASC("Student ID (Ascending)"),
    BY_NAME_ASC("Student Name (A-Z)"),
    BY_YEAR_ASC("Year Level (Lowest to Highest)"),
    BY_YEAR_DESC("Year Level (Highest to Lowest)")
}

/**
 * Data access abstraction for Student entity persistence.
 * Designed to cleanly mirror an Android Room DAO / Repository pattern.
 */
interface StudentRepository {
    fun getAll(): List<Student>
    fun findById(id: String): Student?
    fun searchByName(query: String): List<Student>
    fun insert(student: Student)
    fun update(student: Student)
    fun delete(id: String): Boolean
    fun filterAndSort(
        filterCourse: String? = null,
        filterYear: Int? = null,
        sortBy: StudentSortOption = StudentSortOption.BY_ID_ASC
    ): List<Student>
}
