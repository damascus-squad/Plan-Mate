import io.mockk.*
import logic.model.Admin
import logic.usecase.auth.CreateMateUseCase
import org.damascus.logic.model.Role
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.views.AdminDashboardView
import org.damascus.ui.views.project.ProjectView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

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
        val admin = Admin(
            id = UUID.randomUUID(),
            username = "notAdminUser",
            password = "password",
            role = Role.ADMIN
        )

        // When
        adminDashboardView.showDashboard(admin)

        // Then
        verify(exactly = 1) { consoleDisplay.displayMenu(any(), any()) }
    }
    @Test
    fun `showDashboard should reject a non admin user`() {
        // Given
        val notAdmin = Admin(
            id = UUID.randomUUID(),
            username = "adminUser",
            password = "password",
            role = Role.MATE
        )

        // when
        adminDashboardView.showDashboard(notAdmin)

        // Then
        verify { projectView wasNot Called }
    }
}
