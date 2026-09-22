package com.studentmanagement

import com.studentmanagement.model.EnrolledSubject
import com.studentmanagement.model.Subject
import com.studentmanagement.service.GradeCalculator
import kotlin.test.*

class GradeCalculatorTest {

    @Test
    fun `test GWA calculation with weighted units`() {
        // Subject A: 3 units * 1.50 = 4.50
        // Subject B: 3 units * 2.00 = 6.00
        // Subject C: 2 units * 1.00 = 2.00
        // Total points = 12.50, Total units = 8 -> GWA = 1.5625
        val subjects = listOf(
            EnrolledSubject(Subject("IT101", "Sub A", 3), 1.50),
            EnrolledSubject(Subject("IT102", "Sub B", 3), 2.00),
            EnrolledSubject(Subject("IT103", "Sub C", 2), 1.00)
        )

        val gwa = GradeCalculator.calculateGWA(subjects)
        assertNotNull(gwa)
        assertEquals(1.5625, gwa, 0.0001)
    }

    @Test
    fun `test GWA ignores subjects without grades`() {
        val subjects = listOf(
            EnrolledSubject(Subject("IT101", "Sub A", 3), 1.50),
            EnrolledSubject(Subject("IT102", "Sub B", 3), null)
        )

        val gwa = GradeCalculator.calculateGWA(subjects)
        assertNotNull(gwa)
        assertEquals(1.50, gwa, 0.0001)
    }

    @Test
    fun `test GWA returns null when no subjects have grades`() {
        val subjects = listOf(
            EnrolledSubject(Subject("IT101", "Sub A", 3), null)
        )

        val gwa = GradeCalculator.calculateGWA(subjects)
        assertNull(gwa)
    }

    @Test
    fun `test academic standing evaluation`() {
        assertEquals("President's Lister (Highest Honors)", GradeCalculator.evaluateStanding(1.20, false))
        assertEquals("Dean's Lister (High Honors)", GradeCalculator.evaluateStanding(1.60, false))
        assertEquals("Good Academic Standing", GradeCalculator.evaluateStanding(2.25, false))
        assertEquals("Academic Warning (Has Failing Grade)", GradeCalculator.evaluateStanding(2.00, true))
        assertEquals("Probationary Status", GradeCalculator.evaluateStanding(3.50, false))
        assertEquals("No Completed Subjects", GradeCalculator.evaluateStanding(null, false))
    }
}
