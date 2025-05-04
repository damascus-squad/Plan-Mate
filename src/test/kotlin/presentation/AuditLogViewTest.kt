package presentation

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.NoLogsException
import org.damascus.logic.model.History
import org.damascus.logic.usecase.AuditLogUseCase
import org.damascus.ui.views.AuditLogView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class AuditLogViewTest {

    private lateinit var auditUseCase: AuditLogUseCase
    private lateinit var auditLogView: AuditLogView

    @BeforeEach
    fun setUp() {
        auditUseCase = mockk(relaxed = true)
        auditLogView = AuditLogView(auditUseCase)
    }

    @Test
    fun `should call getLogsByProjectId with valid UUID`() {
        val uuid = UUID.randomUUID()
        val fakeHistory = mockk<History>()
        every { auditUseCase.getLogsByProjectId(uuid) } returns listOf(fakeHistory)

        auditLogView.viewProjectLog(uuid)

        verify { auditUseCase.getLogsByProjectId(uuid) }
    }

    @Test
    fun `should call getLogByTaskId with valid UUID`() {
        val uuid = UUID.randomUUID()
        val fakeHistory = mockk<History>()
        every { auditUseCase.getLogByTaskId(uuid) } returns listOf(fakeHistory)

        auditLogView.viewTaskLog(uuid)

        verify { auditUseCase.getLogByTaskId(uuid) }
    }

    @Test
    fun `should display error when NoLogsException is thrown for project`() {
        val uuid = UUID.randomUUID()
        every { auditUseCase.getLogsByProjectId(uuid) } throws NoLogsException("No logs")

        auditLogView.viewProjectLog(uuid)

        assertThrows<NoLogsException> {
            auditUseCase.getLogsByProjectId(uuid)
        }
    }

    @Test
    fun `should display error when NoLogsException is thrown for task`() {
        val uuid = UUID.randomUUID()
        every { auditUseCase.getLogByTaskId(uuid) } throws NoLogsException("No logs")

        auditLogView.viewTaskLog(uuid)

        assertThrows<NoLogsException> {
            auditUseCase.getLogByTaskId(uuid)
        }
    }
}