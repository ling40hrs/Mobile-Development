package com.studentmanagement.service

import com.studentmanagement.model.EnrolledSubject
import java.util.Locale

data class AcademicSummary(
    val totalEnrolledUnits: Int,
    val totalGradedUnits: Int,
    val totalPassedUnits: Int,
    val gwa: Double?,
    val standing: String
)

object GradeCalculator {

    /** sum(grade * units) / sum(units), using only subjects that have a grade. */
    fun calculateGWA(enrolledSubjects: Collection<EnrolledSubject>): Double? {
        val completed = enrolledSubjects.filter { it.grade != null }
        if (completed.isEmpty()) return null

        val totalWeightedPoints = completed.sumOf { (it.grade ?: 0.0) * it.subject.units }
        val totalUnits = completed.sumOf { it.subject.units }

        return if (totalUnits > 0) totalWeightedPoints / totalUnits else null
    }

    const val STANDING_EXCELLENT = "Excellent"
    const val STANDING_GOOD = "Good"
    const val STANDING_FAILED = "Failed"
    const val STANDING_NONE = "No Completed Subjects"

    /**
     * Failed if there is any 0.00 or GWA under 1.00; Excellent at 3.00+; otherwise Good.
     */
    fun evaluateStanding(gwa: Double?, hasDeficiency: Boolean): String {
        if (gwa == null) return STANDING_NONE

        return when {
            hasDeficiency || gwa < 1.00 -> STANDING_FAILED
            gwa >= 3.00 -> STANDING_EXCELLENT
            else -> STANDING_GOOD
        }
    }

    fun generateSummary(subjects: Collection<EnrolledSubject>): AcademicSummary {
        val gwa = calculateGWA(subjects)
        val totalEnrolled = subjects.sumOf { it.subject.units }
        val completed = subjects.filter { it.grade != null }
        val gradedUnits = completed.sumOf { it.subject.units }
        val passedUnits = completed.filter { it.isPassed }.sumOf { it.subject.units }
        val hasDeficiency = completed.any { !it.isPassed }

        return AcademicSummary(
            totalEnrolledUnits = totalEnrolled,
            totalGradedUnits = gradedUnits,
            totalPassedUnits = passedUnits,
            gwa = gwa,
            standing = evaluateStanding(gwa, hasDeficiency)
        )
    }

    fun format(value: Double?): String {
        return if (value != null) String.format(Locale.US, "%.2f", value) else "N/A"
    }
}
