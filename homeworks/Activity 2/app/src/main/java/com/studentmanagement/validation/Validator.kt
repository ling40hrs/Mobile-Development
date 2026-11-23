package com.studentmanagement.validation

import com.studentmanagement.R
import com.studentmanagement.model.Gender
import com.studentmanagement.model.Student
import com.studentmanagement.model.StudentData
import com.studentmanagement.model.StudentInput
import com.studentmanagement.model.Subject
import com.studentmanagement.model.YearLevel

object Validator {

    const val FIELD_STUDENT = "student"
    const val FIELD_STUDENT_NUMBER = "studentNumber"
    const val FIELD_FIRST_NAME = "firstName"
    const val FIELD_LAST_NAME = "lastName"
    const val FIELD_EMAIL = "email"
    const val FIELD_AGE = "age"
    const val FIELD_CONTACT = "contact"
    const val FIELD_GENDER = "gender"
    const val FIELD_YEAR_LEVEL = "yearLevel"
    const val FIELD_CONFIRMATION = "confirmation"
    const val FIELD_GRADE = "grade"
    const val FIELD_SUBJECTS = "subjects"

    private const val MIN_AGE = 15
    private const val MAX_AGE = 60
    private const val MIN_GRADE = 0.0
    private const val MAX_GRADE = 100.0

    private val STUDENT_NUMBER_PATTERN = Regex("^[A-Za-z0-9][A-Za-z0-9 .-]{1,19}$")
    private val NAME_PATTERN = Regex("^[A-Za-z][A-Za-z .'-]{1,39}$")
    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val CONTACT_PATTERN = Regex("^\\+?[0-9][0-9 -]{5,16}$")

    fun validateRegistration(
        input: StudentInput,
        existing: List<Student>,
        editingId: Int? = null
    ): Validation<StudentData> {
        val errors = mutableListOf<FieldError>()

        val studentNumber = input.studentNumber.trim()
        val firstName = input.firstName.trim()
        val lastName = input.lastName.trim()
        val email = input.email.trim()
        val ageText = input.age.trim()
        val contactNumber = input.contactNumber.trim()

        when {
            studentNumber.isEmpty() ->
                errors += FieldError(FIELD_STUDENT_NUMBER, R.string.err_student_number_required)

            !STUDENT_NUMBER_PATTERN.matches(studentNumber) ->
                errors += FieldError(FIELD_STUDENT_NUMBER, R.string.err_student_number_invalid)

            isDuplicateStudentNumber(studentNumber, existing, editingId) ->
                errors += FieldError(FIELD_STUDENT_NUMBER, R.string.err_student_number_duplicate)
        }

        validateName(firstName, FIELD_FIRST_NAME, R.string.err_first_name_required, R.string.err_first_name_invalid)
            ?.let { errors += it }
        validateName(lastName, FIELD_LAST_NAME, R.string.err_last_name_required, R.string.err_last_name_invalid)
            ?.let { errors += it }

        when {
            email.isEmpty() ->
                errors += FieldError(FIELD_EMAIL, R.string.err_email_required)

            !EMAIL_PATTERN.matches(email) ->
                errors += FieldError(FIELD_EMAIL, R.string.err_email_invalid)

            isDuplicateEmail(email, existing, editingId) ->
                errors += FieldError(FIELD_EMAIL, R.string.err_email_duplicate)
        }

        val age = ageText.toIntOrNull()
        when {
            ageText.isEmpty() -> errors += FieldError(FIELD_AGE, R.string.err_age_required)
            age == null -> errors += FieldError(FIELD_AGE, R.string.err_age_invalid)
            age < MIN_AGE || age > MAX_AGE -> errors += FieldError(FIELD_AGE, R.string.err_age_range)
        }

        if (contactNumber.isEmpty()) {
            errors += FieldError(FIELD_CONTACT, R.string.err_contact_required)
        } else if (!CONTACT_PATTERN.matches(contactNumber) || contactNumber.count { it.isDigit() } !in 7..15) {
            errors += FieldError(FIELD_CONTACT, R.string.err_contact_invalid)
        }

        val gender = input.gender
        if (gender == null) {
            errors += FieldError(FIELD_GENDER, R.string.err_gender_required)
        }

        val yearLevel = input.yearLevel
        if (yearLevel == null) {
            errors += FieldError(FIELD_YEAR_LEVEL, R.string.err_year_required)
        }

        if (!input.confirmed) {
            errors += FieldError(FIELD_CONFIRMATION, R.string.err_confirmation_required)
        }

        if (errors.isNotEmpty()) {
            return Validation.Failure(errors)
        }

        return Validation.Success(
            StudentData(
                studentNumber = studentNumber,
                firstName = firstName,
                lastName = lastName,
                email = email,
                age = age ?: MIN_AGE,
                contactNumber = contactNumber,
                gender = gender ?: Gender.MALE,
                yearLevel = yearLevel ?: YearLevel.FIRST
            )
        )
    }

    fun validateGrade(raw: String): Validation<Double> {
        val text = raw.trim()
        if (text.isEmpty()) {
            return Validation.Failure(FIELD_GRADE, R.string.err_grade_required)
        }
        val grade = text.toDoubleOrNull()
            ?: return Validation.Failure(FIELD_GRADE, R.string.err_grade_invalid)
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            return Validation.Failure(FIELD_GRADE, R.string.err_grade_range)
        }
        return Validation.Success(grade)
    }

    fun validateEnrollment(
        selection: List<Subject>,
        available: List<Subject>
    ): Validation<List<Subject>> {
        if (available.isEmpty()) {
            return Validation.Failure(FIELD_SUBJECTS, R.string.err_all_enrolled)
        }
        if (selection.isEmpty()) {
            return Validation.Failure(FIELD_SUBJECTS, R.string.err_subject_required)
        }
        return Validation.Success(selection)
    }

    private fun validateName(
        value: String,
        field: String,
        requiredRes: Int,
        invalidRes: Int
    ): FieldError? = when {
        value.isEmpty() -> FieldError(field, requiredRes)
        !NAME_PATTERN.matches(value) -> FieldError(field, invalidRes)
        else -> null
    }

    private fun isDuplicateStudentNumber(
        studentNumber: String,
        existing: List<Student>,
        editingId: Int?
    ): Boolean = existing.any {
        it.id != editingId && it.studentNumber.equals(studentNumber, ignoreCase = true)
    }

    private fun isDuplicateEmail(
        email: String,
        existing: List<Student>,
        editingId: Int?
    ): Boolean = existing.any {
        it.id != editingId && it.email.equals(email, ignoreCase = true)
    }
}
