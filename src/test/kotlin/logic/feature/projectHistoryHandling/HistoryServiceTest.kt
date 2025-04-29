package logic.feature.projectHistoryHandling

import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.feature.projectHistoryHandling.HistoryService
import org.damascus.logic.repositories.HistoryRepository
import org.damascus.utiles.HistoryEmptyException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import io.mockk.verify



class HistoryServiceTest {
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyService: HistoryService

    @BeforeEach
    fun setup() {
        historyRepository = mockk(relaxed = true)
        historyService = HistoryService(historyRepository)
    }

    @Test
    fun `should return the actions log when the history not empty`() {
        //Given
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        every { historyRepository.getAllLogs() } returns listOf(
            createFakeActionLog(
                userName = "TestMate",
                currentState = "TODO",
                targetedState = "In-progress",
                taskId = UUID.randomUUID(),
                actionDate = fakeDate,
            ),
            createFakeActionLog(
                userName = "TestMate",
                currentState = "To-do",
                targetedState = "In-progress",
                taskId = UUID.randomUUID(),
                actionDate = fakeDate,
            )
        )
        //When
        val historyLog = historyService.getAllLogs()

        //Then
        assertEquals(2, historyLog.size)

    }

    @Test
    fun `should return HistoryEmptyException when no logs found`() {
        //Given
        every { historyRepository.getAllLogs() } returns emptyList()

        // When & Then
        assertThrows<HistoryEmptyException> {
            historyService.getAllLogs()
        }

    }

    @Test
    fun `should save the actions log when the action valid`() {
        //Given
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userAction =
            createFakeActionLog(
                userName = "TestMate",
                currentState = "TODO",
                targetedState = "In-progress",
                taskId = UUID.randomUUID(),
                actionDate = fakeDate,
            )
        // When
        historyService.saveLog(userAction)

        // Then
        verify(exactly = 1) { historyRepository.saveLog(userAction) }
    }
}