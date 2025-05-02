package logic.useCase

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.State
import org.damascus.logic.model.ActionType
import org.damascus.logic.useCase.AuditLogUseCase
import logic.repository.AuditLogRepository
import org.damascus.logic.model.History.Companion.NO_STATE
import org.damascus.logic.model.History.Companion.NO_UUID
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

class AuditLogUseCaseTest {
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var auditLogUseCase: AuditLogUseCase

    private val todoState = State(UUID.randomUUID(), "TODO")
    private val inProgressState = State(UUID.randomUUID(), "In-progress")
    private val doneState = State(UUID.randomUUID(), "Done")

    @BeforeEach
    fun setup() {
        auditLogRepository = mockk(relaxed = true)
        auditLogUseCase = AuditLogUseCase(
            auditLogRepository,
            stateRepository = TODO()
        )
    }

    @Test
    fun `should return action logs when history is not empty`() {
        // Given
        val id = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val projectId = UUID.randomUUID()
        every { auditLogRepository.getLogsByProjectId(projectId) } returns listOf(
            createFakeActionLog(id, userId, UUID.randomUUID(), projectId, fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED),
            createFakeActionLog(id, userId, UUID.randomUUID(), projectId, fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED)
        )

        // When
        val historyLog = auditLogUseCase.getLogsByProjectId(projectId)

        // Then
        assertEquals(2, historyLog.size)
    }

    @Test
    fun `should throw NoHistoryException when no logs are found`() {
        // Given
        val projectId = UUID.randomUUID()
        every { auditLogRepository.getLogsByProjectId(projectId) } returns emptyList()

        // When & Then
        assertThrows<NoHistoryException> {
            auditLogUseCase.getLogsByProjectId(projectId)
        }
    }

    @Test
    fun `should save action log when the action is valid`() {
        // Given
        val id = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userAction = createFakeActionLog(id, userId, UUID.randomUUID(), UUID.randomUUID(), fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED)

        // When
        auditLogUseCase.saveLog(userAction)

        // Then
        verify(exactly = 1) {
            auditLogRepository.saveLog(match {
                it.currentState == todoState &&
                        it.newState == inProgressState &&
                        it.userId == userId
            })
        }
    }

    @Test
    fun `should throw InvalidStateException when state is not allowed`() {
        // Given
        val id = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val invalidState = State(UUID.randomUUID(), "Unknown")
        val invalidAction = createFakeActionLog(id, userId, UUID.randomUUID(), UUID.randomUUID(), Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), invalidState, doneState, ActionType.TASK_STATE_CHANGED)

        // When & Then
        assertThrows<InvalidStateException> {
            auditLogUseCase.saveLog(invalidAction)
        }
    }

    @Test
    fun `should return action logs when given projectId`() {
        // Given
        val id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val logs = listOf(
            createFakeActionLog(id, userId, UUID.randomUUID(), projectId, fakeDate, todoState, inProgressState, ActionType.TASK_STATE_CHANGED)
        )

        every { auditLogRepository.getLogsByProjectId(projectId) } returns logs

        // When
        val result = auditLogUseCase.getLogsByProjectId(projectId)

        // Then
        assertTrue { result.any {projectId == it.projectId} }    }

    @Test
    fun `should return action logs when given taskId`() {
        // Given
        val id = UUID.randomUUID()
        val taskId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val logs = listOf(
            createFakeActionLog(id, userId, taskId, UUID.randomUUID(), fakeDate, inProgressState, doneState, ActionType.TASK_STATE_CHANGED)
        )

        every { auditLogRepository.getLogByTaskId(taskId) } returns logs

        // When
        val result = auditLogUseCase.getLogByTaskId(taskId)

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
            "TASK_DELETED",
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