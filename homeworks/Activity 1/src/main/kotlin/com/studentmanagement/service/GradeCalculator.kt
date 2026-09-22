package com.studentmanagement.service

import com.studentmanagement.model.EnrolledSubject
import java.util.Locale

/**
 * Detailed academic evaluation summary for a student.
 */
data class AcademicSummary(
    val totalEnrolledUnits: Int,
    val totalGradedUnits: Int,
    val totalPassedUnits: Int,
    val gwa: Double?,
    val standing: String
)

/**
 * Calculates General Weighted Average (GWA) and determines academic honors or standing.
 */
object GradeCalculator {

    /**
     * Calculates the General Weighted Average:
     * sum(grade * units) / sum(units for graded subjects)
     */
    fun calculateGWA(enrolledSubjects: Collection<EnrolledSubject>): Double? {
        val completed = enrolledSubjects.filter { it.grade != null }
        if (completed.isEmpty()) return null

        val totalWeightedPoints = completed.sumOf { (it.grade ?: 0.0) * it.subject.units }
        val totalUnits = completed.sumOf { it.subject.units }

        return if (totalUnits > 0) totalWeightedPoints / totalUnits else null
    }

    /**
     * Determines academic standing based on standard collegiate benchmarks.
     */
    fun evaluateStanding(gwa: Double?, hasDeficiency: Boolean): String {
        if (gwa == null) return "No Completed Subjects"

        return when {
            hasDeficiency -> "Academic Warning (Has Failing Grade)"
            gwa in 1.00..1.45 -> "President's Lister (Highest Honors)"
            gwa in 1.46..1.75 -> "Dean's Lister (High Honors)"
            gwa in 1.76..3.00 -> "Good Academic Standing"
            else -> "Probationary Status"
        }
    }

    /**
     * Generates a complete academic summary for a collection of enrolled subjects.
     */
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
