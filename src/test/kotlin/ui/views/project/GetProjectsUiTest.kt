package ui.views.project

import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import logic.model.User
import logic.model.UserRole
import logic.usecase.project.CreateProjectUseCase
import logic.usecase.project.GetAdminProjectsUseCase
import logic.usecase.project.GetMateProjectsUseCase
import org.junit.jupiter.api.BeforeEach
import ui.io.ConsoleUserInput
import java.util.*

class GetProjectsUiTest {
    private lateinit var consoleUserInput: ConsoleUserInput
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var getAdminProjectsUseCase: GetAdminProjectsUseCase
    private lateinit var getMateProjectsUseCase: GetMateProjectsUseCase
    private lateinit var sampleProject: Project

    @BeforeEach
    fun setup() {
        consoleUserInput = mockk()
        createProjectUseCase = mockk()
        getAdminProjectsUseCase = mockk()
        getMateProjectsUseCase = mockk()

        sampleProject = Project(
            id = UUID.randomUUID(),
            name = "Project 1",
            assignedMatesIds = mutableListOf(UUID.randomUUID()),
            allowedStatesIds = mutableListOf(UUID.randomUUID()),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
    }

//    @Test
//    fun `should throw exception when no projects exist for admin`() {
//        // Given
//        every { getAdminProjectsUseCase() } returns emptyList()
//        every { consoleUserInput.readInt(any(), any(), any()) } throws IllegalStateException()
//        val admin = createUser(UserRole.ADMIN, "admin")
//
//        // When
//        val view = createProjectForUser(admin)
//
//        // Then
//        assertThrows<ProjectsNotAvailableException> {
//            view.showProjects(admin)
//        }
//        verify { getAdminProjectsUseCase() }
//    }

//    @Test
//    fun `should display and select project when projects exist for admin`() {
//        // Given
//        every { getAllProjectsUseCase() } returns listOf(sampleProject)
//        every { consoleUserInput.readInt(any(), 1, 1) } returns 1
//        val admin = createUser(UserRole.ADMIN, "admin")
//
//        // When
//        val view = createProjectForUser(admin)
//        view.showAllProjects(admin)
//
//        // Then
//        verify { getAllProjectsUseCase() }
//        verify { consoleUserInput.readInt(any(), 1, 1) }
//    }
//
//    @Test
//    fun `should display and select project when projects exist for mate`() {
//        // Given
//        val mate = createUser(UserRole.MATE, "Mate 1")
//        every { getAllProjectsByMateIdUseCase(mate.id) } returns listOf(sampleProject)
//        every { consoleUserInput.readInt(any(), 1, 1) } returns 1
//
//        // When
//        val view = createProjectForUser(mate)
//        view.showAllProjects(mate)
//
//        // Then
//        verify { getAllProjectsByMateIdUseCase(mate.id) }
//        verify { consoleUserInput.readInt(any(), 1, 1) }
//    }

//    @Test
//    fun `should throw exception when no projects exist for mate`() {
//        // Given
//        val mate = createUser(UserRole.MATE, "Mate 1")
//        every { getMateProjectsUseCase(mate.id) } returns emptyList()
//        every { consoleUserInput.readInt(any(), any(), any()) } throws IllegalStateException()
//
//        // When
//        val view = createProjectForUser(mate)
//
//        // Then
//        assertThrows<ProjectsNotAvailableException> {
//            view.showProjects(mate)
//        }
//        verify { getMateProjectsUseCase(mate.id) }
//    }

//    private fun createProjectForUser(user: User): GetProjectsUi {
//        return GetProjectsUi(
//            consoleUserInput = consoleUserInput,
//            getAdminProjectsUseCase = getAdminProjectsUseCase,
//            getMateProjectsUseCase = getMateProjectsUseCase,
//            projectDashboardCli = projectDashboardCli
//        )
//    }

    private fun createUser(userRole: UserRole, username: String): User {
        return User(UUID.randomUUID(), username, userRole)
    }
}