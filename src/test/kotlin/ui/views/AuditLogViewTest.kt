package ui.views

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.NoLogsException
import logic.repo.AuditLogsRepository
import org.damascus.logic.model.History
import org.damascus.logic.usecase.AuditLog.GetLogsByProjectIdUseCase
import org.damascus.logic.usecase.AuditLog.GetLogsByTaskIdUseCase
import org.damascus.ui.views.AuditLogView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class AuditLogViewTest {

    private lateinit var auditLogView: AuditLogView
    private lateinit var auditLogRepository: AuditLogsRepository
    private lateinit var getLogsByProjectId: GetLogsByProjectIdUseCase
    private lateinit var getLogsByTaskId: GetLogsByTaskIdUseCase

    @BeforeEach
    fun setUp() {
        auditLogRepository = mockk(relaxed = true)
        getLogsByProjectId = mockk(relaxed = true)
        getLogsByTaskId = mockk(relaxed = true)
        auditLogView = AuditLogView(getLogsByProjectId, getLogsByTaskId)
    }

    @Test
    fun `should call getLogsByProjectId with valid UUID`() {
        val uuid = UUID.randomUUID()
        val fakeHistory = mockk<History>()
        every { getLogsByProjectId(uuid) } returns listOf(fakeHistory)

        auditLogView.viewProjectLog(uuid)

        verify { getLogsByProjectId(uuid) }
    }

    @Test
    fun `should call getLogByTaskId with valid UUID`() {
        val uuid = UUID.randomUUID()
        val fakeHistory = mockk<History>()
        every { getLogsByTaskId(uuid) } returns listOf(fakeHistory)

        auditLogView.viewTaskLog(uuid)

        verify { getLogsByTaskId(uuid) }
    }

    @Test
    fun `should display error when NoLogsException is thrown for project`() {
        val uuid = UUID.randomUUID()
        every { getLogsByProjectId(uuid) } throws NoLogsException("No logs")

        auditLogView.viewProjectLog(uuid)

        assertThrows<NoLogsException> {
            getLogsByProjectId(uuid)
        }
    }

    @Test
    fun `should display error when NoLogsException is thrown for task`() {
        val uuid = UUID.randomUUID()
        every { getLogsByTaskId(uuid) } throws NoLogsException("No logs")

        auditLogView.viewTaskLog(uuid)

        assertThrows<NoLogsException> {
            getLogsByTaskId(uuid)
        }
    }
}