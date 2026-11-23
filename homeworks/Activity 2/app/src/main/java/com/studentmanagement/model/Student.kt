package com.studentmanagement.model

data class Student(
    val id: Int,
    val studentNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val contactNumber: String,
    val gender: Gender,
    val yearLevel: YearLevel,
    val enrollments: MutableList<Enrollment> = mutableListOf()
) {

    val fullName: String
        get() = "$firstName $lastName"

    val avatarInitial: String
        get() = firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "#"

    val averageGrade: Double?
        get() = enrollments
            .mapNotNull { it.grade }
            .takeIf { it.isNotEmpty() }
            ?.average()

    val gradedCount: Int
        get() = enrollments.count { it.isGraded }

    fun matchesSearch(query: String): Boolean {
        val term = query.trim()
        if (term.isEmpty()) return true
        return firstName.contains(term, ignoreCase = true) ||
            lastName.contains(term, ignoreCase = true) ||
            fullName.contains(term, ignoreCase = true) ||
            studentNumber.contains(term, ignoreCase = true)
    }

    fun isEnrolledIn(subject: Subject): Boolean = enrollments.any { it.subject == subject }

    fun enrollmentOf(subject: Subject): Enrollment? = enrollments.firstOrNull { it.subject == subject }

    fun enroll(subject: Subject): Boolean {
        if (isEnrolledIn(subject)) return false
        enrollments.add(Enrollment(subject))
        return true
    }

    fun setGrade(subject: Subject, grade: Double): Boolean {
        val enrollment = enrollmentOf(subject) ?: return false
        enrollment.grade = grade
        return true
    }

    fun unenroll(subject: Subject): Boolean = enrollments.removeAll { it.subject == subject }

    fun availableSubjects(): List<Subject> = Subject.entries.filterNot { isEnrolledIn(it) }
}
