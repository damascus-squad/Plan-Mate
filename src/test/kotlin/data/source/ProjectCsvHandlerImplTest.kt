package data.source

import data.csvDataHelper.CreateProjectHelper.FILE_PATH_PROJECT
import data.csvDataHelper.CreateProjectHelper.buildHandlerProject
import data.csvDataHelper.CreateProjectHelper.createProject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileDataParser
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class ProjectCsvHandlerImplTest {

    private lateinit var handler: CsvHandlerImpl<Project>

    @BeforeTest
    fun setUp() {
        File(FILE_PATH_PROJECT).delete()
        handler = buildHandlerProject()
    }

    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(FILE_PATH_PROJECT)

        // When/Then
        assertTrue(file.exists())
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(FILE_PATH_PROJECT)
        file.parentFile.mkdirs()
        file.writeText("id,name,assignedMates,creationDate\n")

        // When
        buildHandlerProject()

        // Then
        assertEquals("id,name,assignedMates,creationDate", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val project1 = createProject()
        val project2 = createProject()

        // When
        handler.write( listOf(project1, project2))
        val result = handler.read()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(FILE_PATH_PROJECT).writeText("id,name,assignedMates,creationDate\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(FILE_PATH_PROJECT).writeText("id,name,assignedMates,creationDate\ninvalid_line\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(FILE_PATH_PROJECT).delete()

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should update project when project exists`() {
        // Given
        val project1 = createProject()
        val project2 = createProject()
        handler.write( listOf(project1, project2))
        val updated = project2.copy(name = "Updated Project")

        // When
        handler.update( project2.id.toString(), updated)
        val result = handler.read()

        // Then
        assertEquals("Updated Project", result.find { it.id == project2.id }?.name)
    }

    @Test
    fun `should ignore update when project does not exist`() {
        // Given
        val project1 = createProject()
        handler.write( listOf(project1))
        val fakeId = UUID.randomUUID()
        val ghostProject = project1.copy(id = fakeId, name = "Ghost")

        // When
        handler.update( fakeId.toString(), ghostProject)
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertNotEquals("Ghost", result.first().name)
    }

    @Test
    fun `should delete project by id`() {
        // Given
        val project1 = createProject()
        val project2 = createProject()
        handler.write( listOf(project1, project2))

        // When
        handler.delete( project1.id.toString())
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals(project2.id, result.first().id)
    }

    @Test
    fun `should parse valid project`() {
        // Given
        val mateId = UUID.randomUUID()
        val line = "${UUID.randomUUID()},MyProject,$mateId,${
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }"

        // When
        val result = FileDataParser.parseProject(line)

        // Then
        assertEquals("MyProject", result.name)
        assertEquals(mateId, result.assignedMates.first().id)
    }

    @Test
    fun `should handle empty assigned mates`() {
        // Given
        val line =
            "${UUID.randomUUID()},NoMates,,${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}"

        // When
        val result = FileDataParser.parseProject(line)

        // Then
        assertTrue(result.assignedMates.isEmpty())
    }

    @Test
    fun `should throw when project line is invalid`() {
        // Given
        val line = "${UUID.randomUUID()},Incomplete"

        // When/Then
        assertThrows(CsvParsingException::class.java) {
            FileDataParser.parseProject(line)
        }
    }
}
