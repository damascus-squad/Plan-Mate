package data.source

import data.csvDataHelper.CreateTaskHelper.FILE_PATH_TASK
import data.csvDataHelper.CreateTaskHelper.buildHandlerTask
import data.csvDataHelper.CreateTaskHelper.createTask
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

    private lateinit var handler: CsvHandlerImpl<Task>

    @BeforeTest
    fun setUp() {
        File(FILE_PATH_TASK).delete()
        handler = buildHandlerTask()
    }

    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(FILE_PATH_TASK)

        // When
        val result = file.exists()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(FILE_PATH_TASK)
        file.parentFile.mkdirs()
        file.writeText("id,projectId,title,description,assigneeId,stateId,creationDate\n")

        // When
        buildHandlerTask()

        // Then
        assertEquals("id,projectId,title,description,assigneeId,stateId,creationDate", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val task1 = createTask()
        val task2 = createTask()

        // When
        handler.write(listOf(task1, task2))
        val result = handler.read()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(FILE_PATH_TASK).writeText("id,projectId,title,description,assigneeId,stateId,creationDate\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(FILE_PATH_TASK).delete()

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(FILE_PATH_TASK).writeText("id,projectId,title,description,assigneeId,stateId,creationDate\n${UUID.randomUUID()}\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should update task when task exists`() {
        // Given
        val task1 = createTask()
        val task2 = createTask()
        handler.write(listOf(task1, task2))

        val updated = task2.copy(title = "Updated Task")

        // When
        handler.update(task2.id.toString(), updated)
        val result = handler.read()

        // Then
        assertEquals("Updated Task", result.find { it.id == task2.id }?.title)
    }

    @Test
    fun `should ignore update when task does not exist`() {
        // Given
        val task = createTask()
        handler.write(listOf(task))

        val fakeId = createTask().id
        val ghostTask = task.copy(id = fakeId, title = "Ghost")

        // When
        handler.update(fakeId.toString(), ghostTask)
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertNotEquals("Ghost", result.first().title)
    }

    @Test
    fun `should delete task by id`() {
        // Given
        val task1 = createTask()
        val task2 = createTask()
        handler.write(listOf(task1, task2))

        // When
        handler.delete(task1.id.toString())
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals(task2.id, result.first().id)
    }

    @Test
    fun `should serialize task with assignee having null id safely`() {
        // Given
        val assigneeWithNullId = Mate(
            id = UUID.fromString("00000000-0000-0000-0000-000000000000"),
            username = "ali",
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
        assertEquals("", columns[4])
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
}
