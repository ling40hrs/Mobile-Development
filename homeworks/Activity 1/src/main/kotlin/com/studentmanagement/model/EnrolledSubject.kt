package com.studentmanagement.model

/**
 * Status of an enrolled subject based on academic grading.
 */
enum class SubjectStatus(val label: String) {
    IN_PROGRESS("In Progress"),
    PASSED("Passed"),
    FAILED("Failed")
}

/**
 * Represents a student's enrollment in a particular subject, including their grade.
 *
 * Uses the standard Philippine collegiate grading scale:
 * 1.00 - 3.00 : Passed (1.00 is highest / excellent)
 * 3.01 - 5.00 : Failed (5.00 is failing grade)
 * null        : Grade not yet submitted (In Progress)
 */
data class EnrolledSubject(
    val subject: Subject,
    var grade: Double? = null
) {
    val status: SubjectStatus
        get() = when {
            grade == null -> SubjectStatus.IN_PROGRESS
            grade!! <= 3.00 -> SubjectStatus.PASSED
            else -> SubjectStatus.FAILED
        }

    val remarks: String
        get() = when {
            grade == null -> "In Progress"
            grade!! in 1.00..1.25 -> "Excellent"
            grade!! in 1.26..1.75 -> "Very Good"
            grade!! in 1.76..2.50 -> "Good"
            grade!! in 2.51..3.00 -> "Passed"
            else -> "Failed"
        }

    val isCompleted: Boolean
        get() = grade != null

    val isPassed: Boolean
        get() = status == SubjectStatus.PASSED
}
