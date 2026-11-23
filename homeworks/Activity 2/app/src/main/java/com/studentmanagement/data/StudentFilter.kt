package com.studentmanagement.data

import com.studentmanagement.model.Gender
import com.studentmanagement.model.Student
import com.studentmanagement.model.YearLevel

sealed class StudentFilter(val label: String) {

    object All : StudentFilter("All students")

    data class ByYear(val yearLevel: YearLevel) : StudentFilter(yearLevel.label)

    data class ByGender(val gender: Gender) : StudentFilter(gender.label)

    data class ByEnrollment(val enrolled: Boolean) : StudentFilter(
        if (enrolled) "With enrolled subjects" else "Without enrolled subjects"
    )

    object WithGrades : StudentFilter("With recorded grades")

    fun matches(student: Student): Boolean = when (this) {
        is All -> true
        is ByYear -> student.yearLevel == yearLevel
        is ByGender -> student.gender == gender
        is ByEnrollment -> student.enrollments.isNotEmpty() == enrolled
        is WithGrades -> student.gradedCount > 0
    }

    companion object {

        fun options(): List<StudentFilter> = listOf(
            All,
            ByYear(YearLevel.FIRST),
            ByYear(YearLevel.SECOND),
            ByYear(YearLevel.THIRD),
            ByYear(YearLevel.FOURTH),
            ByGender(Gender.MALE),
            ByGender(Gender.FEMALE),
            ByEnrollment(enrolled = true),
            ByEnrollment(enrolled = false),
            WithGrades
        )
    }
}
