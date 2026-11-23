package com.studentmanagement.model

data class Enrollment(val subject: Subject, var grade: Double? = null) {

    val isGraded: Boolean
        get() = grade != null
}
