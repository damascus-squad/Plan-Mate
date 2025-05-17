package org.damascus.logic.usecase.project

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.logic.repo.ProjectRepository
import org.damascus.logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ManageProjectUseCaseTest {

    private lateinit var projectRepo: ProjectRepository
    private lateinit var taskStateRepo: TaskStateRepository
    private lateinit var manageProjectUseCase: ManageProjectUseCase

    private val testProjectId = UUID.randomUUID()
    private val testMate = User(UUID.randomUUID(), "mate", UserRole.MATE)
    private val testProject = Project(
        id = testProjectId,
        name = "Test Project",
        assignedMatesIds = mutableListOf(testMate.id),
        allowedStatesIds = mutableListOf(),
        creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )

    @BeforeEach
    fun setUp() {
        projectRepo = mockk(relaxed = true)
        taskStateRepo = mockk(relaxed = true)
        manageProjectUseCase = ManageProjectUseCase(projectRepo, taskStateRepo)
    }

    @Test
    fun `createProject should call projectRepo create when project doesn't exist`() = runTest {
        // Given
        coEvery { projectRepo.exists(testProjectId) } returns false

        // When
        manageProjectUseCase.createProject(testProject)

        // Then
        coVerify(exactly = 1) { projectRepo.exists(testProjectId) }
        coVerify(exactly = 1) { projectRepo.create(testProject) }
    }

    @Test
    fun `createProject should only call projectRepo exists when project exists`() = runTest {
        // Given
        coEvery { projectRepo.exists(testProjectId) } returns true

        // When
        manageProjectUseCase.createProject(testProject)

        // Then
        coVerify(exactly = 1) { projectRepo.exists(testProjectId) }
        coVerify(exactly = 0) { projectRepo.create(any()) }
    }

    @Test
    fun `getProject should call projectRepo get() when it is called`() = runTest {
        // Given
        coEvery { projectRepo.get(testProjectId) } returns testProject

        // When
        manageProjectUseCase.getProject(testProjectId)

        // Then
        coVerify(exactly = 1) { projectRepo.get(testProjectId) }
    }

    @Test
    fun `getAllProjects should call projectRepo getAll() when called`() = runTest {
        // Given
        coEvery { projectRepo.getAll() } returns listOf(testProject)

        // When
        manageProjectUseCase.getAllProjects()

        // Then
        coVerify(exactly = 1) { projectRepo.getAll() }
    }

    @Test
    fun `getMateProjects should call projectRepo getAllProjectsByMateId() when called`() = runTest {
        // Given
        coEvery { projectRepo.getAllProjectsByMateId(testMate.id) } returns listOf(testProject)

        // When
        manageProjectUseCase.getMateProjects(testMate.id)

        // Then
        coVerify(exactly = 1) { projectRepo.getAllProjectsByMateId(testMate.id) }
    }

    @Test
    fun `updateProject should call projectRepo update() when called`() = runTest {
        // Given
        val updatedProject = testProject.copy(name = "Updated Name")
        coEvery { projectRepo.update(testProjectId, updatedProject) } returns true

        // When
        manageProjectUseCase.updateProject(testProjectId, updatedProject)

        // Then
        coVerify(exactly = 1) { projectRepo.update(testProjectId, updatedProject) }
    }

    @Test
    fun `deleteProject should call projectRepo delete() when called`() = runTest {
        // Given
        coEvery { projectRepo.delete(testProjectId) } returns true

        // When
        manageProjectUseCase.deleteProject(testProjectId)

        // Then
        coVerify(exactly = 1) { projectRepo.delete(testProjectId) }
    }

}


