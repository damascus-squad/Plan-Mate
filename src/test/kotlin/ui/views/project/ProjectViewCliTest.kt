package ui.views.project

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.ProjectsNotAvailableException
import logic.exception.UnauthorizedActionException
import logic.model.Project
import logic.model.User
import logic.model.UserRole
import logic.usecase.project.CreateProjectUseCase
import logic.usecase.project.GetAllProjectsByMateIdUseCase
import logic.usecase.project.GetAllProjectsUseCase
import org.damascus.ui.views.projectDashboard.ProjectDashboardController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ui.io.ConsoleUserInput
import ui.io.InputReader
import java.util.*

class ProjectViewCliTest {
    private lateinit var consoleUserInput: ConsoleUserInput
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllProjectsByMateIdUseCase: GetAllProjectsByMateIdUseCase
    private lateinit var sampleProject: Project
    private lateinit var projectDashboardCli: ProjectDashboardController

    @BeforeEach
    fun setup() {
        consoleUserInput = mockk()
        createProjectUseCase = mockk()
        getAllProjectsUseCase = mockk()
        getAllProjectsByMateIdUseCase = mockk()
        projectDashboardCli = mockk()

        sampleProject = Project(
            id = UUID.randomUUID(),
            name = "Project 1",
            assignedMatesIds = mutableListOf(UUID.randomUUID()),
            allowedStatesIds = mutableListOf(UUID.randomUUID()),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
    }

    @Test
    fun `should throw exception when no projects exist for admin`() {
        // Given
        every { getAllProjectsUseCase() } returns emptyList()
        every { consoleUserInput.readInt(any(), any(), any()) } throws IllegalStateException()
        val admin = createUser(UserRole.ADMIN, "admin")

        // When
        val view = createProjectForUser(admin)

        // Then
        assertThrows<ProjectsNotAvailableException> {
            view.showAllProjects(admin)
        }
        verify { getAllProjectsUseCase() }
    }

    @Test
    fun `should display and select project when projects exist for admin`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        every { consoleUserInput.readInt(any(), 1, 1) } returns 1
        val admin = createUser(UserRole.ADMIN, "admin")

        // When
        val view = createProjectForUser(admin)
        view.showAllProjects(admin)

        // Then
        verify { getAllProjectsUseCase() }
        verify { consoleUserInput.readInt(any(), 1, 1) }
    }

    @Test
    fun `should display and select project when projects exist for mate`() {
        // Given
        val mate = createUser(UserRole.MATE, "Mate 1")
        every { getAllProjectsByMateIdUseCase(mate.id) } returns listOf(sampleProject)
        every { consoleUserInput.readInt(any(), 1, 1) } returns 1

        // When
        val view = createProjectForUser(mate)
        view.showAllProjects(mate)

        // Then
        verify { getAllProjectsByMateIdUseCase(mate.id) }
        verify { consoleUserInput.readInt(any(), 1, 1) }
    }

    @Test
    fun `should throw exception when no projects exist for mate`() {
        // Given
        val mate = createUser(UserRole.MATE, "Mate 1")
        every { getAllProjectsByMateIdUseCase(mate.id) } returns emptyList()
        every { consoleUserInput.readInt(any(), any(), any()) } throws IllegalStateException()

        // When
        val view = createProjectForUser(mate)

        // Then
        assertThrows<ProjectsNotAvailableException> {
            view.showAllProjects(mate)
        }
        verify { getAllProjectsByMateIdUseCase(mate.id) }
    }

    private fun createProjectForUser(user: User): ProjectViewCli {
        return ProjectViewCli(
            consoleUserInput = consoleUserInput,
            getAllProjectsUseCase = getAllProjectsUseCase,
            getAllProjectsByMateIdUseCase = getAllProjectsByMateIdUseCase,
            projectDashboardCli = projectDashboardCli
        )
    }

    private fun createUser(userRole: UserRole, username: String): User {
        return User(UUID.randomUUID(), username, userRole)
    }
}