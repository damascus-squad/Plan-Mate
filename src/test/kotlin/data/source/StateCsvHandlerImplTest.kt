package data.source

import data.csvDataHelper.createState
import data.model.State
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

class StateCsvHandlerImplTest {

    private val filePath = "test_assets/states.csv"
    private lateinit var handler: GenericCsvHandlerImpl<State>

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
        file.writeText("id,name\n")

        // When
        buildHandler()

        // Then
        assertEquals("id,name", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val s1 = createState(UUID.randomUUID(), "TODO")
        val s2 = createState(UUID.randomUUID(), "IN_PROGRESS")

        // When
        handler.write(filePath, listOf(s1, s2))
        val result = handler.read(filePath)

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should update state when state exists`() {
        // Given
        val s1 = createState(UUID.randomUUID(), "TODO")
        val s2 = createState(UUID.randomUUID(), "DOING")
        handler.write(filePath, listOf(s1, s2))

        val updated = s2.copy(name = "IN_PROGRESS")

        // When
        handler.update(filePath, s2.id.toString(), updated)
        val result = handler.read(filePath)

        // Then
        assertEquals("IN_PROGRESS", result.find { it.id == s2.id }?.name)
    }

    @Test
    fun `should ignore update when state does not exist`() {
        // Given
        val s1 = createState(UUID.randomUUID(), "TODO")
        handler.write(filePath, listOf(s1))

        val ghost = createState(UUID.randomUUID(), "GHOST")

        // When
        handler.update(filePath, ghost.id.toString(), ghost)
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("TODO", result.first().name)
    }

    @Test
    fun `should delete state by id`() {
        // Given
        val s1 = createState(UUID.randomUUID(), "TODO")
        val s2 = createState(UUID.randomUUID(), "DONE")
        handler.write(filePath, listOf(s1, s2))

        // When
        handler.delete(filePath, s1.id.toString())
        val result = handler.read(filePath)

        // Then
        assertEquals(1, result.size)
        assertEquals("DONE", result.first().name)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(filePath).writeText("id,name\n")

        // When
        val result = handler.read(filePath)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(filePath).writeText("id,name\ninvalid_line\n")

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

    private fun buildHandler(): GenericCsvHandlerImpl<State> {
        return GenericCsvHandlerImpl(
            filePath = filePath,
            header = "id,name",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseState,
            serializer = FileDataSerializer::serializeState
        )
    }
}
