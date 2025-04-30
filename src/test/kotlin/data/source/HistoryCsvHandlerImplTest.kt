package data.source

import data.csvDataHelper.createHistory
import data.model.History
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
        file.writeText("id,projectID,entityId,entityType,changedBy,oldState,newState,timestamp\n")

        // When
        buildHandler()

        // Then
        assertEquals("id,projectID,entityId,entityType,changedBy,oldState,newState,timestamp", file.readLines().first())
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
        File(filePath).writeText("id,projectID,entityId,entityType,changedBy,oldState,newState,timestamp\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(filePath).writeText("id,projectID,entityId,entityType,changedBy,oldState,newState,timestamp\ninvalid_line\n")

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

    private fun buildHandler(): GenericCsvHandlerImpl<History> {
        return GenericCsvHandlerImpl(
            filePath = filePath,
            header = "id,projectID,entityId,entityType,changedBy,oldState,newState,timestamp",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseHistory,
            serializer = FileDataSerializer::serializeHistory
        )
    }
}
