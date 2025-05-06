import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.UnauthorizedActionException
import logic.model.User
import logic.model.UserRole
import logic.usecase.auth.CreateMateUseCase
import logic.usecase.project.CreateProjectUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ui.io.ConsoleDisplay
import org.damascus.ui.views.admin.AdminDashboardView
import org.junit.jupiter.api.assertThrows
import ui.io.InputReader
import ui.views.project.ProjectView
import ui.views.project.ProjectViewCli
import java.util.*

class AdminDashboardViewTest {
    private lateinit var consoleDisplay: ConsoleDisplay
    private lateinit var projectView: ProjectView
    private lateinit var consoleUserInput: InputReader
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var adminDashboardView: AdminDashboardView
    private lateinit var createMateUseCase: CreateMateUseCase


    @BeforeEach
    fun setup() {
        consoleDisplay = mockk(relaxed = true)
        projectView = mockk(relaxed = true)
        createMateUseCase = mockk(relaxed = true)
        consoleUserInput = mockk(relaxed = true)
        createProjectUseCase = mockk(relaxed = true)
        adminDashboardView = AdminDashboardView(consoleDisplay,consoleUserInput, projectView, createMateUseCase,createProjectUseCase)
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

    @Test
    fun `should throw UnauthorizedActionException when mate tries to create project`() {
        // Given
        val mate = createUser(UserRole.MATE, "Mate 1")

        // When && Then
        assertThrows<UnauthorizedActionException> {
            adminDashboardView.createProject(mate)
        }
    }

    @Test
    fun `should return true when project created successfully`() {
        // Given
        every { consoleUserInput.readString(any()) } returns "Test Project"
        every { createProjectUseCase(any()) } returns true
        val admin = createUser(UserRole.ADMIN, "admin")

        // When
        adminDashboardView.createProject(admin)

        // Then
        verify { createProjectUseCase(match { it.name == "Test Project" }) }
    }

    @Test
    fun `should return false when project already exists`() {
        // Given
        every { consoleUserInput.readString(any()) } returns "Test Project"
        every { createProjectUseCase(any()) } returns false
        val admin = createUser(UserRole.ADMIN, "admin")

        // When
        adminDashboardView.createProject(admin)

        // Then
        verify { createProjectUseCase(any()) }
    }

    private fun createUser(userRole: UserRole, username: String): User {
        return User(UUID.randomUUID(), username, userRole)
    }
}
