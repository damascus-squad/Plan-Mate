package org.damascus.data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.data.dto.HistoryLogDTO
import org.damascus.data.mapper.toModel
import org.damascus.logic.model.ActionType
import org.damascus.logic.repo.DataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AuditLogsRepositoryImplTest {
    private lateinit var dataSource: DataSource<HistoryLogDTO>
    private lateinit var repository: AuditLogsRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = AuditLogsRepositoryImpl(dataSource)
    }

    @Test
    fun `saveLog should write the log to dataSource`() = runTest {
        // Given
        val log = sampleLog

        // When
        repository.saveLog(log.toModel())

        // Then
        coVerify { dataSource.write(log) }
    }

    @Test
    fun `getLogByProjectId should return logs matching projectId`() = runTest {
        // Given
        val log = sampleLog
        val otherLog = sampleLog.copy(projectId = UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(log, otherLog)

        // When
        val result = repository.getLogsByProjectId(log.projectId)

        // Then
        assertThat(result).containsExactly(log.toModel())
    }

    @Test
    fun `getLogByProjectId should return empty list if no match found`() = runTest {
        // Given
        val log = sampleLog.copy(projectId = UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(log)

        // When
        val result = repository.getLogsByProjectId(UUID.randomUUID())

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getLogByTaskId should return logs matching taskId`() = runTest {
        // Given
        val log = sampleLog
        val otherLog = sampleLog.copy(taskId = UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(log, otherLog)

        // When
        val result = repository.getLogsByTaskId(log.taskId)

        // Then
        assertThat(result).containsExactly(log.toModel())
    }

    @Test
    fun `getLogByTaskId should return empty list if no match found`() = runTest {
        // Given
        val log = sampleLog.copy(taskId = UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(log)

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
    ) = HistoryLogDTO(
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