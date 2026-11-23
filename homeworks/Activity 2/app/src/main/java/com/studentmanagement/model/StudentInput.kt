package com.studentmanagement.model

data class StudentInput(
    val studentNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: String,
    val contactNumber: String,
    val gender: Gender?,
    val yearLevel: YearLevel?,
    val confirmed: Boolean
)

data class StudentData(
    val studentNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val age: Int,
    val contactNumber: String,
    val gender: Gender,
    val yearLevel: YearLevel
)
