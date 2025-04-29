package data.source

import data.csvDataHelper.createProject
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProjectCsvHandlerImplTest {

    private val testFilePath = "test_assets/projects_test.csv"
    private lateinit var handler: ProjectCsvHandlerImpl

    @BeforeEach
    fun setUp() {
        File(testFilePath).delete()
        handler = ProjectCsvHandlerImpl(filePath = testFilePath)
    }

    @Test
    fun `should create projects_test csv file if not exists`() {
        // Given
        val file = File(testFilePath)

        // When/Then
        assertTrue(file.exists(), "File should be created at $testFilePath")
    }

    @Test
    fun `should contain correct header in the file`() {
        // Given/When
        val header = File(testFilePath).readLines().firstOrNull()

        // Then
        assertEquals("id,name,assignedMates,creationDate", header)
    }

    @Test
    fun `should read projects correctly from file`() {
        // Given
        val file = File(testFilePath)
        val projectLine = "11111111-1111-1111-1111-111111111111,Test Project,,2025-04-28T12:00:00"
        file.appendText(projectLine + "\n")

        // When
        val result = handler.read(testFilePath)

        // Then
        assertEquals("Test Project", result.first().name)
    }

    @Test
    fun `should write projects correctly to file`() {
        // Given
        val project1 = createProject(name = "Project One")
        val project2 = createProject(name = "Project Two")

        // When
        handler.write(testFilePath, listOf(project1, project2))
        val result = File(testFilePath).readLines().drop(1)

        // Then
        assertTrue(result.size == 2)
    }
}

