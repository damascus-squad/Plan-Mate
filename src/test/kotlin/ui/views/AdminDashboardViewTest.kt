import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import logic.model.User
import logic.model.UserRole
import logic.usecase.auth.CreateMateUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ui.io.ConsoleDisplay
import ui.views.AdminDashboardView
import ui.views.project.ProjectView
import java.util.*

class AdminDashboardViewTest {
    private lateinit var consoleDisplay: ConsoleDisplay
    private lateinit var projectView: ProjectView
    private lateinit var adminDashboardView: AdminDashboardView
    private lateinit var createMateUseCase: CreateMateUseCase


    @BeforeEach
    fun setup() {
        consoleDisplay = mockk(relaxed = true)
        projectView = mockk(relaxed = true)
        createMateUseCase = mockk(relaxed = true)
        adminDashboardView = AdminDashboardView(consoleDisplay, projectView, createMateUseCase)
    }

    @Test
    fun `showDashboard should accept an admin user`() {
        // Given
        val admin = User(
            id = UUID.randomUUID(),
            username = "notAdminUser",
            userRole = UserRole.ADMIN
        )

        // When
        adminDashboardView.showDashboard(admin)

        // Then
        verify(exactly = 1) { consoleDisplay.displayMenu(any(), any()) }
    }

    @Test
    fun `showDashboard should reject a non admin user`() {
        // Given
        val notAdmin = User(
            id = UUID.randomUUID(),
            username = "adminUser",
            userRole = UserRole.MATE
        )

        // when
        adminDashboardView.showDashboard(notAdmin)

        // Then
        verify { projectView wasNot Called }
    }
}
