package com.studentmanagement.data

import com.studentmanagement.model.Student

enum class SortOption(val label: String) {
    NAME_ASC("Name (A to Z)"),
    NAME_DESC("Name (Z to A)"),
    STUDENT_NUMBER("Student number (A to Z)"),
    GRADE_HIGH("Average grade (high to low)"),
    GRADE_LOW("Average grade (low to high)"),
    YEAR_LEVEL("Year level (1st to 4th)");

    fun comparator(): Comparator<Student> = when (this) {
        NAME_ASC -> Comparator { a, b ->
            val result = a.lastName.compareTo(b.lastName, ignoreCase = true)
            if (result != 0) result else a.firstName.compareTo(b.firstName, ignoreCase = true)
        }

        NAME_DESC -> Comparator { a, b ->
            val result = b.lastName.compareTo(a.lastName, ignoreCase = true)
            if (result != 0) result else b.firstName.compareTo(a.firstName, ignoreCase = true)
        }

        STUDENT_NUMBER -> Comparator { a, b ->
            a.studentNumber.compareTo(b.studentNumber, ignoreCase = true)
        }

        GRADE_HIGH -> Comparator { a, b ->
            val gradeA = a.averageGrade ?: -1.0
            val gradeB = b.averageGrade ?: -1.0
            val result = gradeB.compareTo(gradeA)
            if (result != 0) result else a.lastName.compareTo(b.lastName, ignoreCase = true)
        }

        GRADE_LOW -> Comparator { a, b ->
            val gradeA = a.averageGrade ?: 101.0
            val gradeB = b.averageGrade ?: 101.0
            val result = gradeA.compareTo(gradeB)
            if (result != 0) result else a.lastName.compareTo(b.lastName, ignoreCase = true)
        }

        YEAR_LEVEL -> Comparator { a, b ->
            val result = a.yearLevel.ordinal.compareTo(b.yearLevel.ordinal)
            if (result != 0) result else a.lastName.compareTo(b.lastName, ignoreCase = true)
        }
    }
}
