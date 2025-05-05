package logic.useCase.auditLog

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.NoLogException
import logic.repo.AuditLogsRepository
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History.Companion.NO_TASK_STATE
import org.damascus.logic.model.History.Companion.NO_UUID
import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.auditLog.GetLogsByTaskIdUseCase
import org.damascus.logic.usecase.AuditLog.SaveLogUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.*

class AuditLogUseCaseTest {
    private lateinit var auditLogRepository: AuditLogsRepository
    private lateinit var saveLog: SaveLogUseCase
    private lateinit var getLogsByProjectId: GetLogsByProjectIdUseCase
    private lateinit var getLogsByTaskId: GetLogsByTaskIdUseCase

    private val todoStateId = UUID.randomUUID()
    private val inProgressStateId = UUID.randomUUID()
    private val doneStateId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        auditLogRepository = mockk(relaxed = true)
        saveLog = SaveLogUseCase(auditLogRepository)
        getLogsByProjectId = GetLogsByProjectIdUseCase(auditLogRepository)
        getLogsByTaskId = GetLogsByTaskIdUseCase(auditLogRepository)
    }

    @Test
    fun `should return action log when history is not empty`() {
        // Given
        val id = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val projectId = UUID.randomUUID()
        every { auditLogRepository.getLogsByProjectId(projectId) } returns listOf(
            createFakeActionLog(
                id,
                userId,
                UUID.randomUUID(),
                projectId,
                fakeDate,
                todoStateId,
                inProgressStateId,
                ActionType.TASK_STATE_CHANGED
            ),
            createFakeActionLog(
                id,
                userId,
                UUID.randomUUID(),
                projectId,
                fakeDate,
                todoStateId,
                inProgressStateId,
                ActionType.TASK_STATE_CHANGED
            )
        )
        // When
        val historyLog = getLogsByProjectId(projectId)

        // Then
        assertEquals(2, historyLog.size)
    }

    @Test
    fun `should throw NoLogException when no log are found for the given project id`() {
        // Given
        val projectId = UUID.randomUUID()
        every { auditLogRepository.getLogsByProjectId(projectId) } returns emptyList()

        // When & Then
        assertThrows<NoLogException> {
            getLogsByProjectId(projectId)
        }
    }

    @Test
    fun `should throw NoLogException when no log are found for the given task id`() {
        // Given
        val taskId = UUID.randomUUID()
        every { auditLogRepository.getLogsByTaskId(taskId) } returns emptyList()

        // When & Then
        assertThrows<NoLogException> {
            getLogsByTaskId(taskId)
        }
    }

    @Test
    fun `should save action log when the action is valid`() {
        // Given
        val id = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val userAction = createFakeActionLog(
            id,
            userId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            fakeDate,
            todoStateId,
            inProgressStateId,
            ActionType.TASK_STATE_CHANGED
        )
        // When
        saveLog(userAction)

        // Then
        verify(exactly = 1) {
            auditLogRepository.saveLog(match {
                it.currentStateId == todoStateId &&
                        it.newStateId == inProgressStateId &&
                        it.userId == userId
            })
        }
    }

    @Test
    fun `should return action log when given projectId`() {
        // Given
        val id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val log = listOf(
            createFakeActionLog(
                id,
                userId,
                UUID.randomUUID(),
                projectId,
                fakeDate,
                todoStateId,
                inProgressStateId,
                ActionType.TASK_STATE_CHANGED
            )
        )
        every { auditLogRepository.getLogsByProjectId(projectId) } returns log

        // When
        val result = getLogsByProjectId(projectId)

        // Then
        assertTrue { result.any { projectId == it.projectId } }
    }

    @Test
    fun `should return action log when given taskId`() {
        // Given
        val id = UUID.randomUUID()
        val taskId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val log = listOf(
            createFakeActionLog(
                id,
                userId,
                taskId,
                UUID.randomUUID(),
                fakeDate,
                inProgressStateId,
                doneStateId,
                ActionType.TASK_STATE_CHANGED
            )
        )
        every { auditLogRepository.getLogsByTaskId(taskId) } returns log

        // When
        val result = getLogsByTaskId(taskId)

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
            currentStateId = todoStateId,
            targetedStateId = inProgressStateId
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
        assertEquals(name, NO_TASK_STATE.name)
    }
}