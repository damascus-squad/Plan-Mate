package logic.feature.projectHistoryHandling

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.model.State
import org.damascus.logic.entities.ActionType
import org.damascus.logic.feature.projectHistoryHandling.HistoryService
import logic.repository.HistoryRepository
import org.damascus.logic.entities.ActionLog.Companion.NO_STATE
import org.damascus.logic.entities.ActionLog.Companion.NO_UUID
import org.damascus.utiles.NoHistoryException
import org.damascus.utiles.InvalidStateException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.*

class HistoryServiceTest {
    private lateinit var historyRepository: HistoryRepository
    private lateinit var historyService: HistoryService

    private val todoState = State(UUID.randomUUID(), "TODO")
    private val inProgressState = State(UUID.randomUUID(), "In-progress")
    private val doneState = State(UUID.randomUUID(), "Done")

    @BeforeEach
    fun setup() {
        historyRepository = mockk(relaxed = true)
        historyService = HistoryService(historyRepository)
    }

    @Test
    fun `should return action logs when history is not empty`() {
        // Given
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val projectId = UUID.randomUUID()
        every { historyRepository.getLogsByProjectId(projectId) } returns listOf(
            createFakeActionLog(userId, UUID.randomUUID(), projectId, fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED),
            createFakeActionLog(userId, UUID.randomUUID(), projectId, fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED)
        )

        // When
        val historyLog = historyService.getLogsByProjectId(projectId)

        // Then
        assertEquals(2, historyLog.size)
    }

    @Test
    fun `should throw NoHistoryException when no logs are found`() {
        // Given
        val projectId = UUID.randomUUID()
        every { historyRepository.getLogsByProjectId(projectId) } returns emptyList()

        // When & Then
        assertThrows<NoHistoryException> {
            historyService.getLogsByProjectId(projectId)
        }
    }

    @Test
    fun `should save action log when the action is valid`() {
        // Given
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userAction = createFakeActionLog(userId, UUID.randomUUID(), UUID.randomUUID(), fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED)

        // When
        historyService.saveLog(userAction)

        // Then
        verify(exactly = 1) {
            historyRepository.saveLog(match {
                it.currentState == todoState &&
                        it.newState == inProgressState &&
                        it.userId == userId
            })
        }
    }

    @Test
    fun `should throw InvalidStateException when state is not allowed`() {
        // Given
        val userId = UUID.randomUUID()
        val invalidState = State(UUID.randomUUID(), "Unknown")
        val invalidAction = createFakeActionLog(userId, UUID.randomUUID(), UUID.randomUUID(), Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), invalidState, doneState, ActionType.TASK_STATE_CHANGED)

        // When & Then
        assertThrows<InvalidStateException> {
            historyService.saveLog(invalidAction)
        }
    }

    @Test
    fun `should return action logs when given projectId`() {
        // Given
        val projectId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val logs = listOf(
            createFakeActionLog(userId, UUID.randomUUID(), projectId, fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED)
        )

        every { historyRepository.getLogsByProjectId(projectId) } returns logs

        // When
        val result = historyService.getLogsByProjectId(projectId)

        // Then
        assertTrue { result.any {projectId == it.projectId} }    }

    @Test
    fun `should return action logs when given taskId`() {
        // Given
        val taskId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val logs = listOf(
            createFakeActionLog(userId, taskId, UUID.randomUUID(), fakeDate, inProgressState, doneState, ActionType.TASK_STATE_CHANGED)
        )

        every { historyRepository.getLogByTaskId(taskId) } returns logs

        // When
        val result = historyService.getLogByTaskId(taskId)

        // Then
        assertTrue(result.any { it.taskId == taskId })
    }

    @ParameterizedTest
    @EnumSource(
        value = ActionType::class,
        names = [
            "TASK_STATE_CHANGED",
            "PROJECT_CREATED",
            "PROJECT_MODIFIED",
            "PROJECT_DELETED",
            "TASK_CREATED",
            "TASK_DELETED"
        ]
    )
    fun `should create ActionLog with action type when action type is valid`(actionType: ActionType) {
        // Given
        val actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        // When
        val log = createFakeActionLog(
            actionType = actionType,
            actionDate = actionDate,
            currentState = todoState,
            targetedState = inProgressState
        )

        // Then
        assertEquals(actionType, log.actionType)
    }
    @Test
    fun `should validate constants NO_UUID and NO_STATE`() {
        // Given
        val name = "Nothing"

        // When & Then
        assertEquals(UUID(0, 0), NO_UUID)
        assertEquals(name, NO_STATE.name)
    }
}