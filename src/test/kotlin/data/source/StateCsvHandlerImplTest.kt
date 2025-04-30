package data.source

import data.csvDataHelper.CreateStateHelper.FILE_PATH_STATE
import data.csvDataHelper.CreateStateHelper.buildHandlerState
import data.csvDataHelper.CreateStateHelper.createState
import logic.model.State
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test

class StateCsvHandlerImplTest {

    private lateinit var handler: CsvHandlerImpl<State>

    @BeforeTest
    fun setUp() {
        File(FILE_PATH_STATE).delete()
        handler = buildHandlerState()
    }


    @Test
    fun `should create file when file does not exist`() {
        // Given
        val file = File(FILE_PATH_STATE)

        // When
        val result = file.exists()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should keep existing header when file already exists`() {
        // Given
        val file = File(FILE_PATH_STATE)
        file.parentFile.mkdirs()
        file.writeText("id,name\n")

        // When
        buildHandlerState()

        // Then
        assertEquals("id,name", file.readLines().first())
    }

    @Test
    fun `should write and return data correctly when reading`() {
        // Given
        val state1 = createState(UUID.randomUUID(), "TODO")
        val state2 = createState(UUID.randomUUID(), "IN_PROGRESS")

        // When
        handler.write( listOf(state1, state2))
        val result = handler.read()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `should return empty list if file only has header`() {
        // Given
        File(FILE_PATH_STATE).writeText("id,name\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty list when file does not exist`() {
        // Given
        File(FILE_PATH_STATE).delete()

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should skip invalid lines when reading`() {
        // Given
        File(FILE_PATH_STATE).writeText("id,name\ninvalid_line\n")

        // When
        val result = handler.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should update state when state exists`() {
        // Given
        val state1 = createState(UUID.randomUUID(), "TODO")
        val state2 = createState(UUID.randomUUID(), "DOING")
        handler.write(listOf(state1, state2))

        val updated = state2.copy(name = "IN_PROGRESS")

        // When
        handler.update(state2.id.toString(), updated)
        val result = handler.read()

        // Then
        assertEquals("IN_PROGRESS", result.find { it.id == state2.id }?.name)
    }

    @Test
    fun `should ignore update when state does not exist`() {
        // Given
        val state1 = createState(UUID.randomUUID(), "TODO")
        handler.write( listOf(state1))

        val ghost = createState(UUID.randomUUID(), "GHOST")

        // When
        handler.update( ghost.id.toString(), ghost)
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals("TODO", result.first().name)
    }

    @Test
    fun `should delete state by id`() {
        // Given
        val state1 = createState(UUID.randomUUID(), "TODO")
        val state2 = createState(UUID.randomUUID(), "DONE")
        handler.write(listOf(state1, state2))

        // When
        handler.delete( state1.id.toString())
        val result = handler.read()

        // Then
        assertEquals(1, result.size)
        assertEquals("DONE", result.first().name)
    }
}
