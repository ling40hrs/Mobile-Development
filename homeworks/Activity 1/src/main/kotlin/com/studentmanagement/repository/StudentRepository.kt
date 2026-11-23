package com.studentmanagement.repository

import com.studentmanagement.model.Student

enum class StudentSortOption(val displayName: String) {
    BY_ID_ASC("Student ID (Ascending)"),
    BY_ID_DESC("Student ID (Descending)"),
    BY_NAME_ASC("Student Name (A-Z)"),
    BY_NAME_DESC("Student Name (Z-A)"),
    BY_YEAR_ASC("Year Level (1 to 4)"),
    BY_YEAR_DESC("Year Level (4 to 1)"),
    BY_GWA_DESC("GWA / GPA (Highest to Lowest)"),
    BY_GWA_ASC("GWA / GPA (Lowest to Highest)"),
    BY_STANDING("Academic Standing (Excellent -> Good -> Failed)")
}

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
        filterStanding: String? = null,
        sortBy: StudentSortOption = StudentSortOption.BY_ID_ASC
    ): List<Student>
}
