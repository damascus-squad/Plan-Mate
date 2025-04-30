package data.source

import data.csvDataHelper.CreateHistoryHelper.FILE_PATH_HISTORY
import data.csvDataHelper.CreateHistoryHelper.buildHandlerHistory
import data.csvDataHelper.CreateHistoryHelper.createHistory
import logic.model.History
import logic.model.State
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

class HistoryCsvHandlerImplTest {

    private lateinit var handler: CsvHandlerImpl<History>

    @BeforeTest
    fun setUp() {
        File(FILE_PATH_HISTORY).delete()
        handler = buildHandlerHistory()
    }

    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(FILE_PATH_HISTORY)

        // When
        val result = file.exists()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(FILE_PATH_HISTORY)
        file.parentFile.mkdirs()
        file.writeText("id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp\n")

        // When
        buildHandlerHistory()

        // Then
        assertEquals("id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()

        // When
        handler.write(listOf(h1, h2))
        val result = handler.read()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(FILE_PATH_HISTORY).writeText("id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(FILE_PATH_HISTORY).writeText("id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp\ninvalid_line\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(FILE_PATH_HISTORY).delete()

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should update history when it exists`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()
        handler.write(listOf(h1, h2))

        val updated = h2.copy(actionType = "UpdatedEntity")

        // When
        handler.update(h2.id.toString(), updated)
        val result = handler.read()

        // Then
        assertEquals("UpdatedEntity", result.find { it.id == h2.id }?.actionType)
    }

    @Test
    fun `should ignore update when history does not exist`() {
        // Given
        val h1 = createHistory()
        handler.write( listOf(h1))

        val ghost = h1.copy(id = UUID.randomUUID(), actionType = "Ghost")

        // When
        handler.update( ghost.id.toString(), ghost)
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertNotEquals("Ghost", result.first().actionType)
    }

    @Test
    fun `should delete history by id`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()
        handler.write(listOf(h1, h2))

        // When
        handler.delete(h1.id.toString())
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals(h2.id, result.first().id)
    }

    @Test
    fun `should parse history with null oldState`() {
        // Given
        val newStateId = UUID.randomUUID()
        val line =
            "${UUID.randomUUID()},${UUID.randomUUID()},${UUID.randomUUID()},task,${UUID.randomUUID()},,${newStateId},${
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }"

        // When
        val result = FileDataParser.parseHistory(line)

        // Then
        assertNull(result.oldState)
        assertEquals(newStateId, result.newState.id)
    }


    @Test
    fun `should throw when history line is invalid`() {
        // Given
        val line = "invalid,line,with,not,enough,columns"

        // When/Then
        assertThrows(CsvParsingException::class.java) {
            FileDataParser.parseHistory(line)
        }
    }

    @Test
    fun `should serialize history with null oldState and valid newState`() {
        // Given
        val newState = State(UUID.fromString("22222222-2222-2222-2222-222222222222"), "InProgress")
        val history = History(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            taskId = UUID.randomUUID(),
            actionType = "task",
            changedBy = UUID.randomUUID(),
            oldState = null,
            newState = newState,
            timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        // When
        val serialized = FileDataSerializer.serializeHistory(history)

        // Then
        val tokens = serialized.split(",")
        assertEquals("", tokens[5])
        assertEquals("22222222-2222-2222-2222-222222222222", tokens[6])
    }

    @Test
    fun `should serialize history with non-null states`() {
        // Given
        val oldState = State(UUID.fromString("11111111-1111-1111-1111-111111111111"), "TODO")
        val newState = State(UUID.fromString("22222222-2222-2222-2222-222222222222"), "DONE")
        val history = History(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            taskId = UUID.randomUUID(),
            actionType = "task",
            changedBy = UUID.randomUUID(),
            oldState = oldState,
            newState = newState,
            timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        // When
        val result = FileDataSerializer.serializeHistory(history)
        val fields = result.split(",")

        // Then
        assertEquals("11111111-1111-1111-1111-111111111111", fields[5])
        assertEquals("22222222-2222-2222-2222-222222222222", fields[6])
    }

    @Test
    fun `should serialize history with valid oldState and newState ids`() {
        // Given
        val oldState = State(UUID.fromString("11111111-1111-1111-1111-111111111111"), "TODO")
        val newState = State(UUID.fromString("22222222-2222-2222-2222-222222222222"), "DONE")
        val history = History(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            taskId = UUID.randomUUID(),
            actionType = "task",
            changedBy = UUID.randomUUID(),
            oldState = oldState,
            newState = newState,
            timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        // When
        val serialized = FileDataSerializer.serializeHistory(history)

        // Then
        val parts = serialized.split(",")
        assertEquals("11111111-1111-1111-1111-111111111111", parts[5])
        assertEquals("22222222-2222-2222-2222-222222222222", parts[6])
    }

    @Test
    fun `should serialize history with non-null newState and id`() {
        // Given
        val newState = State(UUID.fromString("22222222-2222-2222-2222-222222222222"), "InProgress")
        val history = createHistory(newState = newState)

        // When
        val result = FileDataSerializer.serializeHistory(history)

        // Then
        val tokens = result.split(",")
        assertEquals("22222222-2222-2222-2222-222222222222", tokens[6])
    }
}
