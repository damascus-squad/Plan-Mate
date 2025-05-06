package ui.views.project

import io.mockk.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import logic.model.User
import logic.exception.ProjectsNotAvailableException
import logic.exception.UnauthorizedActionException
import logic.usecase.project.CreateProjectUseCase
import logic.usecase.project.GetAllProjectsByMateIdUseCase
import logic.usecase.project.GetAllProjectsUseCase
import org.damascus.logic.model.Role
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.views.project.ProjectViewCli
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ProjectViewCliTest {

    private lateinit var consoleUserInput: ConsoleUserInput
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var getAllProjectsByMateIdUseCase: GetAllProjectsByMateIdUseCase
    private lateinit var sampleProject: Project

    @BeforeEach
    fun setup() {
        consoleUserInput = mockk()
        createProjectUseCase = mockk()
        getAllProjectsUseCase = mockk()
        getAllProjectsByMateIdUseCase = mockk()

        sampleProject = Project(
            id = UUID.randomUUID(),
            name = "Project 1",
            assignedMatesIds = mutableListOf(UUID.randomUUID()),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
    }

    @Test
    fun `should return true when project created successfully`() {
        // Given
        every { consoleUserInput.readString(any()) } returns "Test Project"
        every { createProjectUseCase(any()) } returns true
        val admin = createUser(Role.ADMIN, "admin")

        // When
        val view = createProjectForUser(admin)
        view.createProject()

        // Then
        verify { createProjectUseCase(match { it.name == "Test Project" }) }
    }

    @Test
    fun `should return false when project already exists`() {
        // Given
        every { consoleUserInput.readString(any()) } returns "Test Project"
        every { createProjectUseCase(any()) } returns false
        val admin = createUser(Role.ADMIN, "admin")

        // When
        val view = createProjectForUser(admin)
        view.createProject()

        // Then
        verify { createProjectUseCase(any()) }
    }

    @Test
    fun `should throw exception when no projects exist for admin`() {
        // Given
        every { getAllProjectsUseCase() } returns emptyList()
        every { consoleUserInput.readInt(any(), any(), any()) } throws IllegalStateException()
        val admin = createUser(Role.ADMIN, "admin")

        // When
        val view = createProjectForUser(admin)

        // Then
        assertThrows<ProjectsNotAvailableException> {
            view.showAllProjects()
        }
        verify { getAllProjectsUseCase() }
    }

    @Test
    fun `should display and select project when projects exist for admin`() {
        // Given
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        every { consoleUserInput.readInt(any(), 1, 1) } returns 1
        val admin = createUser(Role.ADMIN, "admin")

        // When
        val view = createProjectForUser(admin)
        view.showAllProjects()

        // Then
        verify { getAllProjectsUseCase() }
        verify { consoleUserInput.readInt(any(), 1, 1) }
    }

    @Test
    fun `should display and select project when projects exist for mate`() {
        // Given
        val mate = createUser(Role.MATE, "Mate 1")
        every { getAllProjectsByMateIdUseCase(mate.id) } returns listOf(sampleProject)
        every { consoleUserInput.readInt(any(), 1, 1) } returns 1

        // When
        val view = createProjectForUser(mate)
        view.showAllProjects()

        // Then
        verify { getAllProjectsByMateIdUseCase(mate.id) }
        verify { consoleUserInput.readInt(any(), 1, 1) }
    }

    @Test
    fun `should throw UnauthorizedActionException when mate tries to create project`() {
        // Given
        val mate = createUser(Role.MATE, "Mate 1")

        // When
        val view = createProjectForUser(mate)

        // Then
        assertThrows<UnauthorizedActionException> {
            view.createProject()
        }
    }

    @Test
    fun `should throw exception when no projects exist for mate`() {
        // Given
        val mate = createUser(Role.MATE, "Mate 1")
        every { getAllProjectsByMateIdUseCase(mate.id) } returns emptyList()
        every { consoleUserInput.readInt(any(), any(), any()) } throws IllegalStateException()

        // When
        val view = createProjectForUser(mate)

        // Then
        assertThrows<ProjectsNotAvailableException> {
            view.showAllProjects()
        }
        verify { getAllProjectsByMateIdUseCase(mate.id) }
    }

    private fun createProjectForUser(user: User): ProjectViewCli {
        return ProjectViewCli(
            currentUser = user,
            consoleUserInput = consoleUserInput,
            createProjectUseCase = createProjectUseCase,
            getAllProjectsUseCase = getAllProjectsUseCase,
            getAllProjectsByMateIdUseCase = getAllProjectsByMateIdUseCase
        )
    }

    private fun createUser(role: Role, username: String, password: String = "1233"): User {
        return object : User(UUID.randomUUID(), username, password, role) {}
    }

}