package data.source

import data.csvDataHelper.createHistory
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

    private val filePath = "test_assets/history.csv"
    private lateinit var handler: GenericCsvHandlerImpl<History>

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
        file.writeText("id,projectID,taskId,entityType,changedBy,oldState,newState,timestamp\n")

        // When
        buildHandler()

        // Then
        assertEquals("id,projectID,taskId,entityType,changedBy,oldState,newState,timestamp", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()

        // When
        handler.write(filePath, listOf(h1, h2))
        val result = handler.read(filePath)

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should update history when it exists`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()
        handler.write(filePath, listOf(h1, h2))

        val updated = h2.copy(entityType = "UpdatedEntity")

        // When
        handler.update(filePath, h2.id.toString(), updated)
        val result = handler.read(filePath)

        // Then
        assertEquals("UpdatedEntity", result.find { it.id == h2.id }?.entityType)
    }

    @Test
    fun `should ignore update when history does not exist`() {
        // Given
        val h1 = createHistory()
        handler.write(filePath, listOf(h1))

        val ghost = h1.copy(id = UUID.randomUUID(), entityType = "Ghost")

        // When
        handler.update(filePath, ghost.id.toString(), ghost)
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertNotEquals("Ghost", result.first().entityType)
    }

    @Test
    fun `should delete history by id`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()
        handler.write(filePath, listOf(h1, h2))

        // When
        handler.delete(filePath, h1.id.toString())
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals(h2.id, result.first().id)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(filePath).writeText("id,projectID,taskId,entityType,changedBy,oldState,newState,timestamp\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(filePath).writeText("id,projectID,taskId,entityType,changedBy,oldState,newState,timestamp\ninvalid_line\n")

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
    fun `should parse history with null states`() {
        // Given
        val line =
            "${UUID.randomUUID()},${UUID.randomUUID()},${UUID.randomUUID()},task,${UUID.randomUUID()},,,${
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }"

        // When
        val result = FileDataParser.parseHistory(line)

        // Then
        assertNull(result.oldState)
        assertNull(result.newState)
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
    fun `should serialize history with null oldState and newState as empty strings`() {
        // Given
        val history = History(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            taskId = UUID.randomUUID(),
            entityType = "task",
            changedBy = UUID.randomUUID(),
            oldState = null,
            newState = null,
            timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        // When
        val serialized = FileDataSerializer.serializeHistory(history)

        // Then
        val tokens = serialized.split(",")
        assertEquals("", tokens[5], "oldState should be serialized as empty string")
        assertEquals("", tokens[6], "newState should be serialized as empty string")
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
            entityType = "task",
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
            entityType = "task",
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

    private fun buildHandler(): GenericCsvHandlerImpl<History> {
        return GenericCsvHandlerImpl(
            filePath = filePath,
            header = "id,projectID,taskId,entityType,changedBy,oldState,newState,timestamp",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseHistory,
            serializer = FileDataSerializer::serializeHistory
        )
    }
}
