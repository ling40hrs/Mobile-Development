package com.studentmanagement.model

enum class SubjectStatus(val label: String) {
    IN_PROGRESS("In Progress"),
    PASSED("Passed"),
    FAILED("Failed")
}

/**
 * CIIT AY 26-27 grading scale:
 * 4.00 Excellent, 3.75-3.50 Superior, 3.25-3.00 Very Good, 2.75-2.50 Good,
 * 2.25-2.00 Satisfactory, 1.75-1.50 Fair, 1.25-1.00 Passed, 0.00 Failed.
 * null means no grade submitted yet.
 */
data class EnrolledSubject(
    val subject: Subject,
    var grade: Double? = null
) {
    val status: SubjectStatus
        get() = when {
            grade == null -> SubjectStatus.IN_PROGRESS
            grade!! >= 1.00 -> SubjectStatus.PASSED
            else -> SubjectStatus.FAILED
        }

    val remarks: String
        get() = when {
            grade == null -> "In Progress"
            grade!! >= 4.00 -> "Excellent"
            grade!! >= 3.50 -> "Superior"
            grade!! >= 3.00 -> "Very Good"
            grade!! >= 2.50 -> "Good"
            grade!! >= 2.00 -> "Satisfactory"
            grade!! >= 1.50 -> "Fair"
            grade!! >= 1.00 -> "Passed"
            else -> "Failed"
        }

    val isCompleted: Boolean
        get() = grade != null

    val isPassed: Boolean
        get() = status == SubjectStatus.PASSED
}
