package ui.views

import io.mockk.*
import logic.model.Admin
import org.damascus.logic.model.Role
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.AdminDashboardView
import org.damascus.ui.views.project.ProjectView
import logic.usecase.auth.CreateMateUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID


class AdminDashboardViewTest {

    private lateinit var adminDashboardView: AdminDashboardView
    private lateinit var inputReader: InputReader
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
        verify(exactly = 0) { inputReader.readInt(any(), any(), any()) }
    }

    @Test
    fun `should show all projects when option 1 is selected`() {
        // Given
        every { inputReader.readInt("Enter your choice: ", 1, 3) } returns 1 andThen 3

        // When
        adminDashboardView.showDashboard(admin)

        // Then
        verify(exactly = 1) { projectView.showAllProjects() }
    }

    @Test
    fun `should exit dashboard when option 3 is selected`() {
        // Given
        every { inputReader.readInt("Enter your choice: ", 1, 3) } returns 3

        // When
        adminDashboardView.showDashboard(admin)

        // Then
        verify(exactly = 0) { projectView.showAllProjects() }
        verify(exactly = 1) { inputReader.readInt("Enter your choice: ", 1, 3) }
    }
}