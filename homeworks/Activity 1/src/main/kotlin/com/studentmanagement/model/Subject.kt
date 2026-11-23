package com.studentmanagement.model

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
