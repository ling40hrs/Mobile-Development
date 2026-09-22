package com.studentmanagement.model

/**
 * Represents a registered student within the institution.
 *
 * @property id Unique institutional student ID (e.g., "1001")
 * @property firstName Given name
 * @property lastName Family name
 * @property course Degree program acronym (e.g., "BSIT")
 * @property yearLevel Academic year (1 through 4/5)
 * @property enrolledSubjects Map of enrolled subjects keyed by uppercase subject code
 */
data class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val course: String,
    val yearLevel: Int,
    val enrolledSubjects: MutableMap<String, EnrolledSubject> = mutableMapOf()
) {
    val fullName: String
        get() = "$firstName $lastName"

    val subjectList: List<EnrolledSubject>
        get() = enrolledSubjects.values.toList()

    val totalEnrolledUnits: Int
        get() = enrolledSubjects.values.sumOf { it.subject.units }

    val totalGradedUnits: Int
        get() = enrolledSubjects.values.filter { it.grade != null }.sumOf { it.subject.units }

    val totalPassedUnits: Int
        get() = enrolledSubjects.values.filter { it.isPassed }.sumOf { it.subject.units }

    fun enrollSubject(subject: Subject): EnrolledSubject {
        val key = subject.code.uppercase()
        val enrolled = EnrolledSubject(subject)
        enrolledSubjects[key] = enrolled
        return enrolled
    }

    fun getEnrolledSubject(subjectCode: String): EnrolledSubject? {
        return enrolledSubjects[subjectCode.uppercase()]
    }

    fun hasSubject(subjectCode: String): Boolean {
        return enrolledSubjects.containsKey(subjectCode.uppercase())
    }
}
