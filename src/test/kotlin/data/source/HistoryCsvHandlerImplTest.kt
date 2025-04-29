package data.source

import data.csvDataHelper.createHistory
import java.time.LocalDateTime
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryCsvHandlerImplTest {

    private val testFilePath = "test_assets/history_test.csv"
    private lateinit var handler: HistoryCsvHandlerImpl

    @BeforeEach
    fun setUp() {
        File(testFilePath).delete()
        handler = HistoryCsvHandlerImpl(filePath = testFilePath)
    }

    @Test
    fun `should create history_test csv file if not exists`() {
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
        assertEquals("id,projectID,entityId,entityType,changedBy,oldState,newState,timestamp", header)
    }

    @Test
    fun `should read history correctly from file`() {
        // Given
        val file = File(testFilePath)
        val historyLine = "11111111-1111-1111-1111-111111111111," +
                "aaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee," +
                "bbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb," +
                "TASK," +
                "ccccccc-bbbb-bbbb-bbbb-bbbbbbbbbbbb," +
                "TODO," +
                "In Progress," +
                "2025-04-28T12:00:00"
        file.appendText(historyLine + "\n")

        // When
        val result = handler.read(testFilePath)

        // Then
        assertEquals("TASK", result.first().entityType)
    }

    @Test
    fun `should write history correctly to file`() {
        // Given
        val history1 = createHistory(
            id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            projectID = UUID.fromString("22222222-2222-2222-2222-222222222222"),
            entityId = UUID.fromString("33333333-3333-3333-3333-333333333333"),
            entityType = "TASK",
            changedBy = UUID.fromString("44444444-4444-4444-4444-444444444444"),
            oldState = "TODO",
            newState = "In Progress",
            timestamp = LocalDateTime.parse("2025-04-28T12:00:00")
        )
        val history2 = createHistory(
            id = UUID.fromString("55555555-5555-5555-5555-555555555555"),
            projectID = UUID.fromString("66666666-6666-6666-6666-666666666666"),
            entityId = UUID.fromString("77777777-7777-7777-7777-777777777777"),
            entityType = "TASK",
            changedBy = UUID.fromString("88888888-8888-8888-8888-888888888888"),
            oldState = "In Progress",
            newState = "Done",
            timestamp = LocalDateTime.parse("2025-04-28T13:00:00")
        )

        // When
        handler.write(testFilePath, listOf(history1, history2))
        val result = File(testFilePath).readLines().drop(1)

        // Then
        assertTrue(result.size == 2)
    }
}

