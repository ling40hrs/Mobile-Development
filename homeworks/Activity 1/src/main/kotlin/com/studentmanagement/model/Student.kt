package com.studentmanagement.model

import com.studentmanagement.service.GradeCalculator

data class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val course: String,
    val yearLevel: Int,
    val enrolledSubjects: MutableMap<String, EnrolledSubject> = mutableMapOf()
) {
    val fullName: String
        get() = "$firstName $lastName"

    val subjectList: List<EnrolledSubject>
        get() = enrolledSubjects.values.toList()

    val totalEnrolledUnits: Int
        get() = enrolledSubjects.values.sumOf { it.subject.units }

    val totalGradedUnits: Int
        get() = enrolledSubjects.values.filter { it.grade != null }.sumOf { it.subject.units }

    val totalPassedUnits: Int
        get() = enrolledSubjects.values.filter { it.isPassed }.sumOf { it.subject.units }

    val gwa: Double?
        get() = GradeCalculator.calculateGWA(enrolledSubjects.values)

    val academicStanding: String
        get() = GradeCalculator.evaluateStanding(
            gwa = gwa,
            hasDeficiency = enrolledSubjects.values.any { it.isCompleted && !it.isPassed }
        )

    fun enrollSubject(subject: Subject): EnrolledSubject {
        val key = subject.code.uppercase()
        val enrolled = EnrolledSubject(subject)
        enrolledSubjects[key] = enrolled
        return enrolled
    }

    fun getEnrolledSubject(subjectCode: String): EnrolledSubject? {
        return enrolledSubjects[subjectCode.uppercase()]
    }

    fun hasSubject(subjectCode: String): Boolean {
        return enrolledSubjects.containsKey(subjectCode.uppercase())
    }
}
