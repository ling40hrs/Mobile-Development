package com.studentmanagement

import com.studentmanagement.exception.DuplicateStudentException
import com.studentmanagement.model.Student
import com.studentmanagement.repository.InMemoryStudentRepository
import com.studentmanagement.repository.StudentSortOption
import kotlin.test.*

class StudentRepositoryTest {

    private lateinit var repository: InMemoryStudentRepository

    @BeforeTest
    fun setUp() {
        repository = InMemoryStudentRepository(seedInitialData = false)
    }

    @Test
    fun `test insert and findById retrieves correct student`() {
        val student = Student("2001", "Alex", "Mercer", "BSCS", 2)
        repository.insert(student)

        val retrieved = repository.findById("2001")
        assertNotNull(retrieved)
        assertEquals("Alex", retrieved.firstName)
        assertEquals("Mercer", retrieved.lastName)
        assertEquals("BSCS", retrieved.course)
        assertEquals(2, retrieved.yearLevel)
    }

    @Test
    fun `test duplicate student id throws DuplicateStudentException`() {
        val s1 = Student("2001", "Alex", "Mercer", "BSCS", 2)
        val s2 = Student("2001", "Alicia", "Vance", "BSIS", 1)

        repository.insert(s1)
        assertFailsWith<DuplicateStudentException> {
            repository.insert(s2)
        }
    }

    @Test
    fun `test searchByName matches first or last name case-insensitively`() {
        repository.insert(Student("101", "Juan", "Dela Cruz", "BSIS", 2))
        repository.insert(Student("102", "Maria", "Santos", "BSCS", 1))
        repository.insert(Student("103", "Pedro", "Reyes", "BSEMC", 3))

        val lastNameMatch = repository.searchByName("santos")
        assertEquals(1, lastNameMatch.size)
        assertEquals("Maria Santos", lastNameMatch[0].fullName)

        val firstNameMatch = repository.searchByName("JUAN")
        assertEquals(1, firstNameMatch.size)
        assertEquals("Juan Dela Cruz", firstNameMatch[0].fullName)

        val partialMatch = repository.searchByName("re")
        assertEquals(1, partialMatch.size)
        assertEquals("Pedro Reyes", partialMatch[0].fullName)
    }

    @Test
    fun `test delete removes student successfully`() {
        val student = Student("101", "Juan", "Dela Cruz", "BSIS", 2)
        repository.insert(student)

        val deleted = repository.delete("101")
        assertTrue(deleted)
        assertNull(repository.findById("101"))
    }

    @Test
    fun `test sorting strategies`() {
        val s1 = Student("300", "Charlie", "Brown", "BSEMC", 3)
        val s2 = Student("100", "Alice", "Smith", "BSCS", 1)
        val s3 = Student("200", "Bob", "Adams", "BSIS", 2)

        repository.insert(s1)
        repository.insert(s2)
        repository.insert(s3)

        val byId = repository.filterAndSort(sortBy = StudentSortOption.BY_ID_ASC)
        assertEquals(listOf("100", "200", "300"), byId.map { it.id })

        val byName = repository.filterAndSort(sortBy = StudentSortOption.BY_NAME_ASC)
        assertEquals(listOf("Bob Adams", "Charlie Brown", "Alice Smith"), byName.map { it.fullName })

        val byYearDesc = repository.filterAndSort(sortBy = StudentSortOption.BY_YEAR_DESC)
        assertEquals(listOf(3, 2, 1), byYearDesc.map { it.yearLevel })
    }

    @Test
    fun `test filtering by course and year level`() {
        repository.insert(Student("101", "Juan", "Dela Cruz", "BSIS", 2))
        repository.insert(Student("102", "Maria", "Santos", "BSCS", 1))
        repository.insert(Student("103", "Pedro", "Reyes", "BSEMC", 3))
        repository.insert(Student("104", "Anna", "Luna", "BSIS", 2))

        val bsisYear2 = repository.filterAndSort(filterCourse = "BSIS", filterYear = 2)
        assertEquals(2, bsisYear2.size)
        assertTrue(bsisYear2.all { it.course == "BSIS" && it.yearLevel == 2 })
    }
}
