package data.source

import java.util.UUID

import data.csvDataHelper.createProject
import data.model.Project
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.junit.jupiter.api.Assertions.*
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test

class ProjectCsvHandlerImplTest {

    private val filePath = "test_assets/projects.csv"
    private lateinit var handler: GenericCsvHandlerImpl<Project>

    @BeforeTest
    fun setUp() {
        File(filePath).delete()
        handler = buildHandler()
    }

    @Test
    fun `should create file when file does not exist`() {
        val file = File(filePath)
        assertTrue(file.exists())
    }

    @Test
    fun `should keep existing header when file already exists`() {
        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeText("id,name,assignedMates,creationDate\n")
        buildHandler()
        assertEquals("id,name,assignedMates,creationDate", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        val project1 = createProject()
        val project2 = createProject()
        handler.write(filePath, listOf(project1, project2))
        val result = handler.read(filePath)
        assertEquals(2, result.size)
    }

    @Test
    fun `should update project when project exists`() {
        val project1 = createProject()
        val project2 = createProject()
        handler.write(filePath, listOf(project1, project2))

        val updated = project2.copy(name = "Updated Project")
        handler.update(filePath, project2.id.toString(), updated)
        val result = handler.read(filePath)

        assertEquals("Updated Project", result.find { it.id == project2.id }?.name)
    }

    @Test
    fun `should ignore update when project does not exist`() {
        val project1 = createProject()
        handler.write(filePath, listOf(project1))

        val fakeId = UUID.randomUUID()
        val ghostProject = project1.copy(id = fakeId, name = "Ghost")
        handler.update(filePath, fakeId.toString(), ghostProject)
        val result = handler.read(filePath)

        assertEquals(1, result.size)
        assertNotEquals("Ghost", result.first().name)
    }

    @Test
    fun `should delete project by id`() {
        val project1 = createProject()
        val project2 = createProject()
        handler.write(filePath, listOf(project1, project2))
        handler.delete(filePath, project1.id.toString())
        val result = handler.read(filePath)
        assertEquals(1, result.size)
        assertEquals(project2.id, result.first().id)
    }

    @Test
    fun `should return empty list if file only has header`() {
        File(filePath).writeText("id,name,assignedMates,creationDate\n")
        val result = handler.read(filePath)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        File(filePath).writeText("id,name,assignedMates,creationDate\ninvalid_line\n")
        val result = handler.read(filePath)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when file does not exist`() {
        File(filePath).delete()
        val result = handler.read(filePath)
        assertTrue(result.isEmpty())
    }

    private fun buildHandler(): GenericCsvHandlerImpl<Project> {
        return GenericCsvHandlerImpl(
            filePath = filePath,
            header = "id,name,assignedMates,creationDate",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseProject,
            serializer = FileDataSerializer::serializeProject
        )
    }
}
