package com.studentmanagement.ui

import com.studentmanagement.model.Student
import com.studentmanagement.service.GradeCalculator
import com.studentmanagement.service.StudentReport

/**
 * Console table and report formatting utility.
 */
object TableFormatter {

    /**
     * Renders student collection in the format required by the specification:
     * =================================
     *               STUDENT LIST
     * ===================================
     * ID       NAME                 COURSE     YEAR
     * -----------------------------------------------
     * 1001     Juan Dela Cruz       BSIT       2
     */
    fun printStudentList(students: List<Student>, title: String = "STUDENT LIST") {
        println()
        println("=================================")
        println(title.padStart((33 + title.length) / 2))
        println("===================================")
        println()

        if (students.isEmpty()) {
            println("  (No student records found)")
            println()
            return
        }

        println("%-8s %-20s %-10s %s".format("ID", "NAME", "COURSE", "YEAR"))
        println("-".repeat(47))

        for (student in students) {
            val truncatedName = if (student.fullName.length > 20) {
                student.fullName.substring(0, 17) + "..."
            } else {
                student.fullName
            }
            println("%-8s %-20s %-10s %d".format(
                student.id,
                truncatedName,
                student.course,
                student.yearLevel
            ))
        }
        println("-".repeat(47))
        println("Total Students: ${students.size}")
        println()
    }

    /**
     * Formats an individual student's comprehensive academic grade report.
     */
    fun printStudentReport(report: StudentReport) {
        val s = report.student
        val summary = report.summary

        println()
        println("=================================================================")
        println("                    STUDENT ACADEMIC REPORT CARD                 ")
        println("=================================================================")
        println("  Student ID   : ${s.id}")
        println("  Student Name : ${s.fullName}")
        println("  Program      : ${s.course}")
        println("  Year Level   : Year ${s.yearLevel}")
        println("-----------------------------------------------------------------")
        println("%-10s %-28s %-6s %-8s %s".format("CODE", "SUBJECT TITLE", "UNITS", "GRADE", "REMARKS"))
        println("-----------------------------------------------------------------")

        if (report.subjects.isEmpty()) {
            println("  (No subjects currently enrolled)")
        } else {
            for (item in report.subjects) {
                val titleTruncated = if (item.subject.title.length > 28) {
                    item.subject.title.substring(0, 25) + "..."
                } else {
                    item.subject.title
                }
                val gradeStr = item.grade?.let { GradeCalculator.format(it) } ?: "---"
                println("%-10s %-28s %-6d %-8s %s".format(
                    item.subject.code,
                    titleTruncated,
                    item.subject.units,
                    gradeStr,
                    item.remarks
                ))
            }
        }

        println("=================================================================")
        println("  Total Units Enrolled : ${summary.totalEnrolledUnits}")
        println("  Total Units Graded   : ${summary.totalGradedUnits}")
        println("  Total Units Passed   : ${summary.totalPassedUnits}")
        println("  General Weighted Avg : ${GradeCalculator.format(summary.gwa)}")
        println("  Academic Standing    : ${summary.standing}")
        println("=================================================================")
        println()
    }
}
