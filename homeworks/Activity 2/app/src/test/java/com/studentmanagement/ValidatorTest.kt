package com.studentmanagement

import com.studentmanagement.model.Gender
import com.studentmanagement.model.StudentInput
import com.studentmanagement.model.YearLevel
import com.studentmanagement.validation.Validation
import com.studentmanagement.validation.Validator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
class ValidatorTest {

    private fun validInput(): StudentInput = StudentInput(
        studentNumber = "2024-1001",
        firstName = "Jose",
        lastName = "Rizal",
        email = "jose.rizal@school.edu",
        age = "21",
        contactNumber = "09170000001",
        gender = Gender.MALE,
        yearLevel = YearLevel.THIRD,
        confirmed = true
    )

    @Test
    fun `valid registration input succeeds`() {
        val result = Validator.validateRegistration(validInput(), emptyList())
        assertTrue(result is Validation.Success)
    }

    @Test
    fun `empty fields produce registration errors`() {
        val blank = StudentInput("", "", "", "", "", "", null, null, false)
        val result = Validator.validateRegistration(blank, emptyList())
        require(result is Validation.Failure)
        val errors = result.errors
        assertTrue(errors.any { it.field == Validator.FIELD_STUDENT_NUMBER })
        assertTrue(errors.any { it.field == Validator.FIELD_FIRST_NAME })
        assertTrue(errors.any { it.field == Validator.FIELD_LAST_NAME })
        assertTrue(errors.any { it.field == Validator.FIELD_EMAIL })
        assertTrue(errors.any { it.field == Validator.FIELD_AGE })
        assertTrue(errors.any { it.field == Validator.FIELD_CONTACT })
        assertTrue(errors.any { it.field == Validator.FIELD_GENDER })
        assertTrue(errors.any { it.field == Validator.FIELD_YEAR_LEVEL })
        assertTrue(errors.any { it.field == Validator.FIELD_CONFIRMATION })
    }

    @Test
    fun `invalid email is rejected`() {
        val result = Validator.validateRegistration(validInput().copy(email = "not-an-email"), emptyList())
        require(result is Validation.Failure)
        assertEquals(Validator.FIELD_EMAIL, result.errors.first().field)
    }

    @Test
    fun `age outside range is rejected`() {
        val young = Validator.validateRegistration(validInput().copy(age = "10"), emptyList())
        assertTrue(young is Validation.Failure)
        val old = Validator.validateRegistration(validInput().copy(age = "61"), emptyList())
        assertTrue(old is Validation.Failure)
        val accepted = Validator.validateRegistration(validInput().copy(age = "18"), emptyList())
        assertTrue(accepted is Validation.Success)
    }

    @Test
    fun `grade inside range is accepted`() {
        assertTrue(Validator.validateGrade("85.5") is Validation.Success)
        assertTrue(Validator.validateGrade("0") is Validation.Success)
        assertTrue(Validator.validateGrade("100") is Validation.Success)
        assertTrue(Validator.validateGrade("100.1") is Validation.Failure)
        assertTrue(Validator.validateGrade("-1") is Validation.Failure)
        assertTrue(Validator.validateGrade("") is Validation.Failure)
        assertTrue(Validator.validateGrade("eighty") is Validation.Failure)
    }

    @Test
    fun `missing gender and year level are rejected`() {
        val missing = Validator.validateRegistration(
            validInput().copy(gender = null, yearLevel = null),
            emptyList()
        )
        require(missing is Validation.Failure)
        val fields = missing.errors.map { it.field }
        assertTrue(fields.contains(Validator.FIELD_GENDER))
        assertTrue(fields.contains(Validator.FIELD_YEAR_LEVEL))
    }
}
