package com.studentmanagement.exception

/**
 * Base sealed exception for all domain-specific errors in the system.
 */
sealed class StudentManagementException(message: String) : RuntimeException(message)

class StudentNotFoundException(val studentId: String) :
    StudentManagementException("Student with ID '$studentId' was not found.")

class DuplicateStudentException(val studentId: String) :
    StudentManagementException("A student with ID '$studentId' already exists in the system.")

class SubjectAlreadyEnrolledException(val studentId: String, val subjectCode: String) :
    StudentManagementException("Student '$studentId' is already enrolled in subject '$subjectCode'.")

class SubjectNotFoundException(val subjectCode: String) :
    StudentManagementException("Subject with code '$subjectCode' was not found.")

class InvalidGradeException(val grade: Double, val details: String = "Grade must be between 1.00 (highest) and 5.00 (failing).") :
    StudentManagementException("Invalid grade '$grade': $details")

class ValidationException(message: String) :
    StudentManagementException(message)
