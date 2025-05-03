import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import logic.usecase.AuditLogUseCase
import logic.usecase.util.NoLogsException
import org.damascus.presentation.audit.AuditLogUi

class AuditLogUiTest {

    private lateinit var getLogs: AuditLogUseCase
    private lateinit var ui: AuditLogUi

    @BeforeEach
    fun setUp() {
        getLogs = mockk()
        ui = AuditLogUi(getLogs)
    }

    @Test
    fun `getLogsByProjectId - valid ID - should print log`() {
        val id = UUID.randomUUID()
        every { getLogs.getLogsByProjectId(id) } returns "Sample Project Log"

        ui.showLogForProject(id)

        verify(exactly = 1) { getLogs.getLogsByProjectId(id) }
    }

    @Test
    fun `getLogsByProjectId - no logs - should catch exception`() {
        val id = UUID.randomUUID()
        every { getLogs.getLogsByProjectId(id) } throws NoLogsException("No log")

        ui.showLogForProject(id)

        verify(exactly = 1) { getLogs.getLogsByProjectId(id) }
    }

    @Test
    fun `getLogsByTaskId - valid ID - should print log`() {
        val id = UUID.randomUUID()
        every { getLogs.getLogsByTaskId(id) } returns "Sample Task Log"

        ui.showLogForTask(id)

        verify(exactly = 1) { getLogs.getLogsByTaskId(id) }
    }

    @Test
    fun `getLogsByTaskId - no logs - should catch exception`() {
        val id = UUID.randomUUID()
        every { getLogs.getLogsByTaskId(id) } throws NoLogsException("No log")

        ui.showLogForTask(id)

        verify(exactly = 1) { getLogs.getLogsByTaskId(id) }
    }
}
