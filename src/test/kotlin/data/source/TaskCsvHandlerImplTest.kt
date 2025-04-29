package data.source

import data.csvDataHelper.createTask
import data.model.State
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

class TaskCsvHandlerImplTest {
    private val testFilePath = "test_assets/tasks_test.csv"
    private lateinit var handler: TaskCsvHandlerImpl

    @BeforeEach
    fun setUp() {
        File(testFilePath).delete()
        handler = TaskCsvHandlerImpl(filePath = testFilePath)
    }

    @Test
    fun `should create tasks_test csv file if not exists`() {
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
        assertEquals("id,projectId,title,description,assigneeId,stateId,creationDate", header)
    }

    @Test
    fun `should read tasks correctly from file`() {
        // Given
        val file = File(testFilePath)
        val task = createTask(
            id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            projectId = UUID.fromString("aaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
            title = "TaskTitle",
            description = "desc",
            state = State(UUID.fromString("bbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "TODO"),
            creationDate = LocalDateTime.parse("2025-04-28T12:00:00")
        )
        file.appendText("${task.id},${task.projectId},${task.title},${task.description},${task.assignee?.id ?: ""},${task.state.id},${task.creationDate}\n")

        // When
        val result = handler.read(testFilePath)

        // Then
        assertEquals("TaskTitle", result.first().title)
    }

    @Test
    fun `should write tasks correctly to file`() {
        // Given
        val task1 = createTask(title = "Task One")
        val task2 = createTask(title = "Task Two")

        // When
        handler.write(testFilePath, listOf(task1, task2))
        val result = File(testFilePath).readLines().drop(1)

        // Then
        assertTrue(result.size == 2)
    }
}
