package com.studentmanagement.ui

import com.studentmanagement.model.Student
import com.studentmanagement.service.GradeCalculator
import com.studentmanagement.service.StudentReport

object TableFormatter {

    fun printStudentList(students: List<Student>, title: String = "STUDENT LIST") {
        val bannerWidth = maxOf(78, title.length)
        println()
        println("=".repeat(bannerWidth))
        println(title.padStart((bannerWidth + title.length) / 2))
        println("=".repeat(bannerWidth))
        println()

        if (students.isEmpty()) {
            println("  (No student records found)")
            println()
            return
        }

        println("%-12s %-22s %-8s %-6s %-8s %s".format("ID", "NAME", "COURSE", "YEAR", "GWA", "STANDING"))
        println("-".repeat(bannerWidth))

        for (student in students) {
            val truncatedName = if (student.fullName.length > 22) {
                student.fullName.substring(0, 19) + "..."
            } else {
                student.fullName
            }
            val truncatedId = if (student.id.length > 12) {
                student.id.substring(0, 9) + "..."
            } else {
                student.id
            }
            val gwaStr = GradeCalculator.format(student.gwa)
            val standingStr = student.academicStanding
            println("%-12s %-22s %-8s %-6d %-8s %s".format(
                truncatedId,
                truncatedName,
                student.course,
                student.yearLevel,
                gwaStr,
                standingStr
            ))
        }
        println("-".repeat(bannerWidth))
        println("Total Students: ${students.size}")
        println()
    }

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
