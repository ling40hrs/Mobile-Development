package com.studentmanagement

import com.studentmanagement.repository.InMemoryStudentRepository
import com.studentmanagement.service.EnrollmentService
import com.studentmanagement.ui.ConsoleApp

/**
 * Application entry point for the Student Enrollment & Grade Management System.
 */
fun main() {
    val repository = InMemoryStudentRepository(seedInitialData = true)
    val service = EnrollmentService(repository)
    val app = ConsoleApp(repository, service)

    app.start()
}
