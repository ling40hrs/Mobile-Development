package com.studentmanagement.model

/**
 * Represents an academic subject offering.
 *
 * @property code Unique subject identifier (e.g., "IT101", "CS102")
 * @property title Descriptive name of the subject
 * @property units Academic unit credit for the subject (typically 1 to 5)
 */
data class Subject(
    val code: String,
    val title: String,
    val units: Int
) {
    init {
        require(code.isNotBlank()) { "Subject code cannot be blank." }
        require(title.isNotBlank()) { "Subject title cannot be blank." }
        require(units > 0) { "Subject units must be greater than zero." }
    }
}
