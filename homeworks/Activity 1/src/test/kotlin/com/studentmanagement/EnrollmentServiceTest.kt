package com.studentmanagement

import com.studentmanagement.exception.InvalidGradeException
import com.studentmanagement.exception.SubjectAlreadyEnrolledException
import com.studentmanagement.exception.ValidationException
import com.studentmanagement.repository.InMemoryStudentRepository
import com.studentmanagement.service.EnrollmentService
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class EnrollmentServiceTest {

    private lateinit var repository: InMemoryStudentRepository
    private lateinit var service: EnrollmentService

    @BeforeTest
    fun setUp() {
        repository = InMemoryStudentRepository(seedInitialData = false)
        service = EnrollmentService(repository)
    }

    @Test
    fun `test registerStudent success with valid courses and year levels 1 to 4`() {
        val student = service.registerStudent("1001", "Juan", "Dela Cruz", "BSIS", 2)
        assertEquals("1001", student.id)
        assertEquals("Juan Dela Cruz", student.fullName)
        assertEquals("BSIS", student.course)
        assertEquals(2, student.yearLevel)

        val bscsStudent = service.registerStudent("1002", "Maria", "Santos", "BSCS", 1)
        assertEquals("BSCS", bscsStudent.course)

        val bsemcStudent = service.registerStudent("1003", "Pedro", "Reyes", "BSEMC", 4)
        assertEquals("BSEMC", bsemcStudent.course)
        assertEquals(4, bsemcStudent.yearLevel)
    }

    @Test
    fun `test registerStudent rejects BSIT and other unlisted courses`() {
        // BSIT must be rejected
        val exBsit = assertFailsWith<ValidationException> {
            service.registerStudent("1004", "Test", "User", "BSIT", 1)
        }
        assertTrue(exBsit.message!!.contains("Invalid course 'BSIT'"))

        // Other invalid courses
        assertFailsWith<ValidationException> {
            service.registerStudent("1005", "Test", "User", "BSCE", 2)
        }
    }

    @Test
    fun `test registerStudent enforces year level strictly 1 to 4`() {
        // Year 0 should fail
        assertFailsWith<ValidationException> {
            service.registerStudent("1006", "Test", "User", "BSCS", 0)
        }

        // Year 5 should fail (only 1-4 allowed)
        assertFailsWith<ValidationException> {
            service.registerStudent("1007", "Test", "User", "BSCS", 5)
        }

        // Empty ID should fail
        assertFailsWith<ValidationException> {
            service.registerStudent("", "Test", "User", "BSCS", 1)
        }
    }

    @Test
    fun `test enrollSubject and prevent duplicates`() {
        service.registerStudent("1001", "Juan", "Dela Cruz", "BSIS", 2)

        val enrolled = service.enrollSubject("1001", "IS101", "Intro to IS", 3)
        assertEquals("IS101", enrolled.subject.code)
        assertEquals(3, enrolled.subject.units)

        // Duplicate enrollment
        assertFailsWith<SubjectAlreadyEnrolledException> {
            service.enrollSubject("1001", "IS101", "Intro to IS", 3)
        }
    }

    @Test
    fun `test recordGrade validation`() {
        service.registerStudent("1001", "Juan", "Dela Cruz", "BSIS", 2)
        service.enrollSubject("1001", "IS101", "Intro to IS", 3)

        val updated = service.recordGrade("1001", "IS101", 1.75)
        assertEquals(1.75, updated.grade)
        assertEquals("Very Good", updated.remarks)

        // Invalid grade outside 1.00..5.00
        assertFailsWith<InvalidGradeException> {
            service.recordGrade("1001", "IS101", 0.50)
        }
        assertFailsWith<InvalidGradeException> {
            service.recordGrade("1001", "IS101", 5.50)
        }
    }

    @Test
    fun `test generateStudentReportAsync coroutine execution`() = runBlocking {
        service.registerStudent("1001", "Juan", "Dela Cruz", "BSIS", 2)
        service.enrollSubject("1001", "IS101", "Intro to IS", 3)
        service.recordGrade("1001", "IS101", 1.25)

        val report = service.generateStudentReportAsync("1001")
        assertNotNull(report)
        assertEquals("1001", report.student.id)
        assertEquals(1, report.subjects.size)
        assertEquals(1.25, report.summary.gwa)
    }

    @Test
    fun `test performAuditAsync coroutine concurrency`() = runBlocking {
        service.registerStudent("1001", "Juan", "Dela Cruz", "BSIS", 2)
        service.registerStudent("1002", "Maria", "Santos", "BSCS", 1)

        val audit = service.performAuditAsync()
        assertEquals(2, audit.totalStudents)
        assertEquals(1, audit.courseBreakdown["BSIS"])
        assertEquals(1, audit.courseBreakdown["BSCS"])
    }
}
