package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.repo.DataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AuditLogsRepositoryImplTest {
    private lateinit var dataSource: DataSource<History>
    private lateinit var repository: AuditLogsRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = AuditLogsRepositoryImpl(dataSource)
    }

    @Test
    fun `saveLog should write the log to dataSource`() {
        // Given
        val log = sampleLog

        // When
        repository.saveLog(log)

        // Then
        verify(exactly = 1) { dataSource.write(log) }
    }

    @Test
    fun `getLogByProjectId should return logs matching projectId`() {
        // Given
        val log = sampleLog
        val otherLog = sampleLog.copy(projectId = UUID.randomUUID())
        every { dataSource.read() } returns listOf(log, otherLog)

        // When
        val result = repository.getLogsByProjectId(log.projectId)

        // Then
        assertThat(result).containsExactly(log)
    }

    @Test
    fun `getLogByProjectId should return empty list if no match found`() {
        // Given
        val log = sampleLog.copy(projectId = UUID.randomUUID())
        every { dataSource.read() } returns listOf(log)

        // When
        val result = repository.getLogsByProjectId(UUID.randomUUID())

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getLogByTaskId should return logs matching taskId`() {
        // Given
        val log = sampleLog
        val otherLog = sampleLog.copy(taskId = UUID.randomUUID())
        every { dataSource.read() } returns listOf(log, otherLog)

        // When
        val result = repository.getLogsByTaskId(log.taskId)

        // Then
        assertThat(result).containsExactly(log)
    }

    @Test
    fun `getLogByTaskId should return empty list if no match found`() {
        // Given
        val log = sampleLog.copy(taskId = UUID.randomUUID())
        every { dataSource.read() } returns listOf(log)

        // When
        val result = repository.getLogsByTaskId(UUID.randomUUID())

        // Then
        assertThat(result).isEmpty()
    }

    private val sampleLog = createFakeActionLog(
        actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        currentStateId = "TODO",
        targetedStateId = "IN PROGRESS"
    )

    private fun createFakeActionLog(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        actionDate: LocalDateTime,
        currentStateId: String,
        targetedStateId: String,
        actionType: ActionType = ActionType.TASK_STATE_CHANGED,
    ): History = History(
        id = id,
        taskId = taskId,
        projectId = projectId,
        currentState = currentStateId,
        newState = targetedStateId,
        actionDate = actionDate,
        actionType = actionType,
        userId = userId,
    )
}