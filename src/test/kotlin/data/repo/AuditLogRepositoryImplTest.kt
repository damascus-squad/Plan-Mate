package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.repo.DataSource
import logic.useCase.createFakeActionLog
import org.damascus.data.repo.AuditLogRepositoryImpl
import org.damascus.logic.model.History
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AuditLogRepositoryImplTest {
    private lateinit var dataSource: DataSource<History>
    private lateinit var repository: AuditLogRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = AuditLogRepositoryImpl(dataSource)
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
        val result = repository.getLogByProjectId(log.projectId)

        // Then
        assertThat(result).containsExactly(log)
    }

    @Test
    fun `getLogByProjectId should return empty list if no match found`() {
        // Given
        val log = sampleLog.copy(projectId = UUID.randomUUID())
        every { dataSource.read() } returns listOf(log)

        // When
        val result = repository.getLogByProjectId(UUID.randomUUID())

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
        val result = repository.getLogByTaskId(log.taskId)

        // Then
        assertThat(result).containsExactly(log)
    }

    @Test
    fun `getLogByTaskId should return empty list if no match found`() {
        // Given
        val log = sampleLog.copy(taskId = UUID.randomUUID())
        every { dataSource.read() } returns listOf(log)

        // When
        val result = repository.getLogByTaskId(UUID.randomUUID())

        // Then
        assertThat(result).isEmpty()
    }

    private val sampleLog = createFakeActionLog(
        actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        currentStateId = UUID.randomUUID(),
        targetedStateId = UUID.randomUUID()
    )
}