package logic.feature.projectHistoryHandling

import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.feature.projectHistoryHandling.HistoryService
import org.damascus.logic.repositories.HistoryRepository
import org.damascus.utiles.NoHistoryException
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
                projectId = UUID.randomUUID(),
                actionDate = fakeDate,
            ),
            createFakeActionLog(
                userName = "TestMate",
                currentState = "To-do",
                targetedState = "In-progress",
                taskId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
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
        assertThrows<NoHistoryException> {
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
                projectId = UUID.randomUUID(),
                actionDate = fakeDate,
            )
        // When
        historyService.saveLog(userAction)

        // Then
        val validStates = listOf("TODO", "In-progress", "Done")

        verify(exactly = 1) {
            historyRepository.saveLog(match {
                it.currentState in validStates &&
                        it.targetedState in validStates &&
                        it.userName == "TestMate"
            })
        }
    }
    @Test
    fun `should return action logs for the given projectId`() {
        // Given
        val projectId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val logs = listOf(
            createFakeActionLog(
                userName = "Tester",
                currentState = "TODO",
                targetedState = "In-progress",
                taskId = UUID.randomUUID(),
                projectId = projectId,
                actionDate = fakeDate
            )
        )

        every { historyRepository.getLogByProjectId(projectId) } returns logs

        // When
        val result = historyService.getLogByProjectId(projectId)

        // Then
        assertEquals(1, result.size)
        assertEquals(projectId, result.first().projectId)
    }
    @Test
    fun `should return action logs for the given taskId`() {
        // Given
        val taskId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val logs = listOf(
            createFakeActionLog(
                userName = "Tester",
                taskId = taskId,
                currentState = "In-progress",
                targetedState = "Done",
                projectId = UUID.randomUUID(),
                actionDate = fakeDate
            )
        )

        every { historyRepository.getLogByTaskId(taskId) } returns logs

        // When
        val result = historyService.getLogByTaskId(taskId)

        // Then
        assertEquals(1, result.size)
        assertEquals(taskId, result.first().taskId)
    }
}