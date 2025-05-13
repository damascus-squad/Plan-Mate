package logic.usecase.auditLog

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.NoLogException
import logic.model.ActionType
import logic.model.History
import logic.repo.AuditLogsRepository
import org.junit.jupiter.api.Assertions
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

    private val todoState = "TODO"
    private val inProgressState = "IN Progress"
    private val doneState = "Done"

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
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        val projectId = UUID.randomUUID()
        every { auditLogRepository.getLogsByProjectId(projectId) } returns listOf(
            createFakeActionLog(
                id,
                userId,
                UUID.randomUUID(),
                projectId,
                fakeDate,
                todoState,
                inProgressState,
                ActionType.TASK_STATE_CHANGED
            ),
            createFakeActionLog(
                id,
                userId,
                UUID.randomUUID(),
                projectId,
                fakeDate,
                todoState,
                inProgressState,
                ActionType.TASK_STATE_CHANGED
            )
        )
        // When
        val historyLog = getLogsByProjectId(projectId)

        // Then
        Assertions.assertEquals(2, historyLog.size)
    }

    @Test
    fun `should throw NoHistoryException when no log are found for the given project id`() {
        // Given
        val projectId = UUID.randomUUID()
        every { auditLogRepository.getLogsByProjectId(projectId) } returns emptyList()

        // When & Then
        assertThrows<NoLogException> {
            getLogsByProjectId(projectId)
        }
    }

    @Test
    fun `should throw NoHistoryException when no log are found for the given task id`() {
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
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        val userAction = createFakeActionLog(
            id,
            userId,
            UUID.randomUUID(),
            UUID.randomUUID(),
            fakeDate,
            todoState,
            inProgressState,
            ActionType.TASK_STATE_CHANGED
        )
        // When
        saveLog(userAction)

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
    fun `should return action log when given projectId`() {
        // Given
        val id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        val log = listOf(
            createFakeActionLog(
                id,
                userId,
                UUID.randomUUID(),
                projectId,
                fakeDate,
                todoState,
                inProgressState,
                ActionType.TASK_STATE_CHANGED
            )
        )
        every { auditLogRepository.getLogsByProjectId(projectId) } returns log

        // When
        val result = getLogsByProjectId(projectId)

        // Then
        Assertions.assertTrue { result.any { projectId == it.projectId } }
    }

    @Test
    fun `should return action log when given taskId`() {
        // Given
        val id = UUID.randomUUID()
        val taskId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val fakeDate = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        val log = listOf(
            createFakeActionLog(
                id,
                userId,
                taskId,
                UUID.randomUUID(),
                fakeDate,
                inProgressState,
                doneState,
                ActionType.TASK_STATE_CHANGED
            )
        )
        every { auditLogRepository.getLogsByTaskId(taskId) } returns log

        // When
        val result = getLogsByTaskId(taskId)

        // Then
        Assertions.assertTrue(result.any { it.taskId == taskId })
    }

    @ParameterizedTest
    @EnumSource(
        value = ActionType::class,
        names = [
            "PROJECT_CREATED",
            "PROJECT_TITLE_MODIFIED",
            "PROJECT_ASSIGNED_USER",
            "PROJECT_UNASSIGNED_USER",
            "PROJECT_DELETED",
        ]
    )
    fun `should create ActionLog with action type when action type is valid`(actionType: ActionType) {
        // Given
        val actionDate = Clock.System.now().toLocalDateTime(TimeZone.Companion.currentSystemDefault())

        // When
        val log = createFakeActionLog(
            actionType = actionType,
            actionDate = actionDate,
            currentState = "TODO",
            newState = "IN PROGRESS"
        )
        // Then
        Assertions.assertEquals(actionType, log.actionType)
    }

    @Test
    fun `should validate constants NO_UUID and NO_STATE`() {
        // Given
        val name = "Nothing"

        // When & Then
        Assertions.assertEquals(UUID(0, 0), History.Companion.NO_UUID)
        Assertions.assertEquals(name, History.Companion.NO_TASK_STATE.name)
    }

    private fun createFakeActionLog(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        actionDate: LocalDateTime,
        currentState: String,
        newState: String,
        actionType: ActionType = ActionType.TASK_STATE_CHANGED,
    ): History = History(
        id = id,
        taskId = taskId,
        projectId = projectId,
        currentState = currentState,
        newState = newState,
        actionDate = actionDate,
        actionType = actionType,
        userId = userId,
    )
}