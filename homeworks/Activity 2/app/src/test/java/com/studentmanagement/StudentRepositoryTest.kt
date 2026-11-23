package com.studentmanagement

import com.studentmanagement.data.SortOption
import com.studentmanagement.data.StudentFilter
import com.studentmanagement.data.StudentQuery
import com.studentmanagement.data.StudentRepository
import com.studentmanagement.model.Gender
import com.studentmanagement.model.StudentInput
import com.studentmanagement.model.YearLevel
import com.studentmanagement.validation.Validation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StudentRepositoryTest {

    private fun input(
        number: String = "2024-2001",
        firstName: String = "Andres",
        lastName: String = "Bonifacio",
        email: String = "andres.bonifacio@school.edu",
        gender: Gender = Gender.MALE,
        yearLevel: YearLevel = YearLevel.SECOND
    ): StudentInput = StudentInput(
        studentNumber = number,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = "22",
        contactNumber = "09170000002",
        gender = gender,
        yearLevel = yearLevel,
        confirmed = true
    )

    @Before
    fun resetRepository() {
        StudentRepository.clear()
    }

    @Test
    fun `registration stores the student`() {
        val result = StudentRepository.save(input())
        assertTrue(result is Validation.Success)
        assertEquals(1, StudentRepository.size)
    }

    @Test
    fun `invalid registration is rejected without storing`() {
        val result = StudentRepository.save(input().copy(firstName = ""))
        assertTrue(result is Validation.Failure)
        assertEquals(0, StudentRepository.size)
    }

    @Test
    fun `duplicate student number is rejected`() {
        assertTrue(StudentRepository.save(input()) is Validation.Success)
        val duplicate = StudentRepository.save(input(email = "unique@school.edu"))
        assertTrue(duplicate is Validation.Failure)
        assertEquals(1, StudentRepository.size)
    }

    @Test
    fun `editing ignores its own identity for duplicates`() {
        val saved = StudentRepository.save(input())
        require(saved is Validation.Success)
        val id = saved.value.id
        val edited = StudentRepository.save(input(firstName = "Apolinario"), editingId = id)
        require(edited is Validation.Success)
        assertEquals("Apolinario", edited.value.firstName)
        assertEquals(1, StudentRepository.size)
    }

    @Test
    fun `removal deletes the student`() {
        val saved = StudentRepository.save(input())
        require(saved is Validation.Success)
        val id = saved.value.id
        assertTrue(StudentRepository.remove(id))
        assertEquals(0, StudentRepository.size)
        assertFalse(StudentRepository.remove(id))
    }

    @Test
    fun `search matches first and last name`() {
        StudentRepository.save(input())
        assertEquals(1, StudentRepository.search(StudentQuery(search = "andres")).size)
        assertEquals(1, StudentRepository.search(StudentQuery(search = "BONIFACIO")).size)
        assertEquals(1, StudentRepository.search(StudentQuery(search = "Andres Bonifacio")).size)
        assertEquals(0, StudentRepository.search(StudentQuery(search = "jacinto")).size)
    }

    @Test
    fun `sort and filter apply to results`() {
        StudentRepository.save(input(number = "2024-2002", firstName = "Gabriela", lastName = "Silang", email = "gabriela.silang@school.edu", gender = Gender.FEMALE, yearLevel = YearLevel.FIRST))
        StudentRepository.save(input())
        val filtered = StudentRepository.search(
            StudentQuery(filter = StudentFilter.ByYear(YearLevel.FIRST))
        )
        assertEquals(1, filtered.size)
        assertEquals("Gabriela", filtered.first().firstName)
        val sorted = StudentRepository.search(StudentQuery(sort = SortOption.NAME_DESC))
        assertEquals("Silang", sorted.first().lastName)
    }
}
