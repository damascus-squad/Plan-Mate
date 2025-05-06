package data.csv

import com.google.common.truth.Truth.assertThat
import data.csv.CsvTestHelper.HISTORY_FILE_PATH
import data.csv.CsvTestHelper.createHistory
import logic.model.ActionType
import logic.model.History
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class CsvDataSourceTest {

    private lateinit var csvDataSource: CsvDataSource<History>
    private val file = File(HISTORY_FILE_PATH)

    @BeforeEach
    fun setUp() {
        file.delete()
        csvDataSource = CsvTestHelper.getHistoryCsvHandler()
    }

    @AfterEach
    fun cleanUp() {
        file.delete()
    }

    @Test
    fun `write should add correct number rows when taking a list`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()

        // When
        csvDataSource.write(listOf(h1, h2))
        val result = csvDataSource.read()

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `write should add a row when taking exactly one valid entry`() {
        // Given
        val h1 = createHistory()

        // When
        csvDataSource.write(h1)
        val result = csvDataSource.read()

        // Then
        assertEquals(1, result.size)
    }

    @Test
    fun `read should return empty list if csv file only contains header`() {
        // Given
        file.writeText("id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp\n")

        // When
        val result = csvDataSource.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `read should return empty list if csv file only contains blank lines`() {
        // Given
        file.writeText("id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp\n\n\n\n\n")

        // When
        val result = csvDataSource.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `read should skip invalid lines when parsing`() {
        // Given
        file.writeText(
            "id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp\ninvalid_line\n"
        )

        // When
        val result = csvDataSource.read()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `read should throw CsvFileNotFound exception when file does not exist`() {
        // Given
        file.delete()

        // When && Then
        assertThrows<CsvFileNotFound> {
            csvDataSource.read()
        }
    }

    @Test
    fun `update should modify entry when it already exists`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()
        csvDataSource.write(listOf(h1, h2))

        // When
        val updated = h2.copy(actionType = ActionType.TASK_STATE_CHANGED)
        csvDataSource.update(h2.id, updated)
        val result = csvDataSource.read()

        // Then
        assertEquals(ActionType.TASK_STATE_CHANGED, result.find { it.id == h2.id }?.actionType)
    }

//    @Test
//    fun `update should throw CsvEntryNotFound exception when entry does not exist`() {
//        // Given
//        val h1 = createHistory()
//        csvDataSource.write(listOf(h1))
//
//        val ghost = h1.copy(id = UUID.randomUUID(), actionType = "Ghost")
//
//        // When && Then
//        assertThrows<CsvEntryNotFound> {
//            csvDataSource.update(ghost.id, ghost)
//        }
//    }

    @Test
    fun `delete should remove entry when id exists`() {
        // Given
        val h1 = createHistory()
        val h2 = createHistory()
        csvDataSource.write(listOf(h1, h2))

        // When
        csvDataSource.delete(h1.id)
        val result = csvDataSource.read()

        // Then
        assertThat(result).containsExactly(h2)
    }

//    @Test
//    fun `delete should throw CsvEntryNotFound exception when entry does not exist`() {
//        // Given
//        val h1 = createHistory()
//        csvDataSource.write(listOf(h1))
//
//        val ghost = h1.copy(id = UUID.randomUUID(), actionType = "Ghost")
//
//        // When && Then
//        assertThrows<CsvEntryNotFound> {
//            csvDataSource.delete(ghost.id)
//        }
//    }
}
