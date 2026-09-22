package com.studentmanagement.model

/**
 * Standard degree programs offered in the system.
 * Only BSCS, BSEMC, and BSIS are permitted.
 */
enum class Course(val code: String, val description: String) {
    BSCS("BSCS", "Bachelor of Science in Computer Science"),
    BSEMC("BSEMC", "Bachelor of Science in Entertainment and Multimedia Computing"),
    BSIS("BSIS", "Bachelor of Science in Information Systems");

    companion object {
        val VALID_YEAR_RANGE: IntRange = 1..4

        fun fromCode(code: String): Course? =
            entries.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }

        fun availableCodes(): List<String> = entries.map { it.code }

        fun isValidCourse(code: String): Boolean = fromCode(code) != null

        fun isValidYear(year: Int): Boolean = year in VALID_YEAR_RANGE
    }
}
