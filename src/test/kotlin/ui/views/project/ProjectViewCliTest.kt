package ui.views.project

import io.mockk.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsUseCase
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.views.project.ProjectViewCli
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectViewCliTest {

    private lateinit var consoleUserInput: ConsoleUserInput
    private lateinit var createProjectUseCase: CreateProjectUseCase
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var projectView: ProjectViewCli

    private lateinit var sampleProject: Project

    @BeforeEach
    fun setup() {
        consoleUserInput = mockk()
        createProjectUseCase = mockk()
        getAllProjectsUseCase = mockk()
        projectView = ProjectViewCli(consoleUserInput, createProjectUseCase, getAllProjectsUseCase)

        sampleProject = Project(
            id = UUID.randomUUID(),
            name = "Project 1",
            assignedMatesIds = mutableListOf(UUID.randomUUID()),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )
    }

    @Test
    fun `should return true when project created successfully`() {
        // given
        every { consoleUserInput.readString(any()) } returns "Test Project"
        every { createProjectUseCase(any()) } returns true

        // when
        projectView.createProject()

        // then
        verify { createProjectUseCase(match { it.name == "Test Project" }) }
    }

    @Test
    fun `should return false when project already exists`() {
        // given
        every { consoleUserInput.readString(any()) } returns "Test Project"
        every { createProjectUseCase(any()) } returns false

        // when
        projectView.createProject()

        // then
        verify { createProjectUseCase(any()) }
    }

    @Test
    fun `should return empty list when no projects exist`() {
        // given
        every { getAllProjectsUseCase() } returns emptyList()

        // when
        projectView.showAllProjects()

        // then
        verify { getAllProjectsUseCase() }
    }

    @Test
    fun `should display and select project when projects exist`() {
        // given
        every { getAllProjectsUseCase() } returns listOf(sampleProject)
        every { consoleUserInput.readInt(any(), any(), any()) } returns 1

        // when
        projectView.showAllProjects()

        // then
        verify { getAllProjectsUseCase() }
        verify { consoleUserInput.readInt(any(), 1, 1) }
    }
}
