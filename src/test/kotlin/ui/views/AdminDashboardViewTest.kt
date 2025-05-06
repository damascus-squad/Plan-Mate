import io.mockk.*
import logic.model.Admin
import logic.usecase.auth.CreateMateUseCase
import org.damascus.logic.model.Role
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.util.UiAction
import org.damascus.ui.views.AdminDashboardView
import org.damascus.ui.views.project.ProjectView
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class AdminDashboardViewTest {

    private lateinit var adminDashboardView: AdminDashboardView
    private lateinit var inputReader: ConsoleUserInput
    private lateinit var projectView: ProjectView
    private lateinit var createMateUseCase: CreateMateUseCase
    private lateinit var admin: Admin

    @BeforeEach
    fun setup() {
        inputReader = mockk(relaxed = true)
        projectView = mockk(relaxed = true)
        createMateUseCase = mockk(relaxed = true)

        admin = Admin(
            id = UUID.randomUUID(),
            username = "adminUser",
            password = "password",
            role = Role.ADMIN
        )

        adminDashboardView = AdminDashboardView(inputReader, projectView, createMateUseCase)
    }

    @Test
    fun `should reject non-admin users`() {
        // Given
        val nonAdmin = Admin(
            id = UUID.randomUUID(),
            username = "nonAdmin",
            password = "password",
            role = Role.MATE
        )

        // When
        adminDashboardView.showDashboard(nonAdmin)

        // Then
        verify { projectView wasNot Called }
    }

    @Test
    fun `should show all projects when option 1 is selected`() {
        // Given
        mockkConstructor(org.damascus.ui.io.ConsoleDisplay::class)

        val actionsSlot = slot<List<UiAction>>()
        val titleSlot = slot<String>()

        every {
            anyConstructed<org.damascus.ui.io.ConsoleDisplay>().displayMenu(
                capture(actionsSlot),
                capture(titleSlot)
            )
        } answers {
            actionsSlot.captured[0].action()
        }

        // When
        adminDashboardView.showDashboard(admin)

        // Then
        verify(exactly = 1) { projectView.showAllProjects() }
    }

    @Test
    fun `should exit menu when option 0 is selected`() {
        // Given
        mockkConstructor(org.damascus.ui.io.ConsoleDisplay::class)

        every {
            anyConstructed<org.damascus.ui.io.ConsoleDisplay>().displayMenu(
                any(), any()
            )
        } returns Unit

        // When
        adminDashboardView.showDashboard(admin)

        // Then
        verify(exactly = 1) {
            anyConstructed<org.damascus.ui.io.ConsoleDisplay>().displayMenu(any(), any())
        }
    }

}
