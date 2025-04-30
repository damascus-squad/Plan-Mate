package data.source

import data.csvDataHelper.createTask
import logic.model.Mate
import logic.model.Task
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.damascus.logic.model.Role
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

class TaskCsvHandlerImplTest {

    private val filePath = "test_assets/tasks.csv"
    private lateinit var handler: GenericCsvHandlerImpl<Task>

    @BeforeTest
    fun setUp() {
        File(filePath).delete()
        handler = buildHandler()
    }

    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(filePath)

        // When
        val result = file.exists()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(filePath)
        file.parentFile.mkdirs()
        file.writeText("id,projectId,title,description,assigneeId,stateId,creationDate\n")

        // When
        buildHandler()

        // Then
        assertEquals("id,projectId,title,description,assigneeId,stateId,creationDate", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val task1 = createTask()
        val task2 = createTask()

        // When
        handler.write(filePath, listOf(task1, task2))
        val result = handler.read(filePath)

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should update task when task exists`() {
        // Given
        val task1 = createTask()
        val task2 = createTask()
        handler.write(filePath, listOf(task1, task2))

        val updated = task2.copy(title = "Updated Task")

        // When
        handler.update(filePath, task2.id.toString(), updated)
        val result = handler.read(filePath)

        // Then
        assertEquals("Updated Task", result.find { it.id == task2.id }?.title)
    }

    @Test
    fun `should ignore update when task does not exist`() {
        // Given
        val task1 = createTask()
        handler.write(filePath, listOf(task1))

        val fakeId = createTask().id
        val ghostTask = task1.copy(id = fakeId, title = "Ghost")

        // When
        handler.update(filePath, fakeId.toString(), ghostTask)
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertNotEquals("Ghost", result.first().title)
    }

    @Test
    fun `should delete task by id`() {
        // Given
        val task1 = createTask()
        val task2 = createTask()
        handler.write(filePath, listOf(task1, task2))

        // When
        handler.delete(filePath, task1.id.toString())
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals(task2.id, result.first().id)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(filePath).writeText("id,projectId,title,description,assigneeId,stateId,creationDate\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(filePath).writeText("id,projectId,title,description,assigneeId,stateId,creationDate\ninvalid_line\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(filePath).delete()

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should serialize task with assignee having null id safely`() {
        // Given
        val assigneeWithNullId = Mate(
            id = UUID.fromString("00000000-0000-0000-0000-000000000000"),
            username = "test",
            password = "test",
            role = Role.MATE
        )

        val task = createTask(assignee = assigneeWithNullId)

        // When
        val serialized = FileDataSerializer.serializeTask(task)

        // Then
        val fields = serialized.split(",")
        assertEquals(7, fields.size)
        assertEquals("00000000-0000-0000-0000-000000000000", fields[4])
    }

    @Test
    fun `should serialize task with non-null assignee but null id using reflection`() {
        // Given
        val assignee = Mate(UUID.randomUUID(), "hacked", "hacked", Role.MATE)

        // User
        val idField = assignee::class.java.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(assignee, null)

        val task = createTask(assignee = assignee)

        // When
        val serialized = FileDataSerializer.serializeTask(task)

        // Then
        val columns = serialized.split(",")
        assertEquals("", columns[4], "Expected blank column for null id even if assignee is not null")
    }

    @Test
    fun `should parse valid task with and without assignee`() {
        val taskWithAssignee = FileDataParser.parseTask(
            "${UUID.randomUUID()},${UUID.randomUUID()},Task,Desc,${UUID.randomUUID()},${UUID.randomUUID()},${
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }"
        )
        assertNotNull(taskWithAssignee.assignee)

        val taskWithoutAssignee = FileDataParser.parseTask(
            "${UUID.randomUUID()},${UUID.randomUUID()},Task,Desc,,${UUID.randomUUID()},${
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }"
        )
        assertNull(taskWithoutAssignee.assignee)
    }

    @Test
    fun `should throw when task line is invalid`() {
        val line = "${UUID.randomUUID()},${UUID.randomUUID()},Task"
        assertThrows(CsvParsingException::class.java) {
            FileDataParser.parseTask(line)
        }
    }

    private fun buildHandler(): GenericCsvHandlerImpl<Task> {
        return GenericCsvHandlerImpl(
            filePath = filePath,
            header = "id,projectId,title,description,assigneeId,stateId,creationDate",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseTask,
            serializer = { FileDataSerializer.serializeTask(it) }
        )
    }
}
