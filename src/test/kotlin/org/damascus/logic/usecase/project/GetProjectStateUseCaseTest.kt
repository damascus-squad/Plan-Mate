package org.damascus.logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.damascus.logic.exception.NoTasksFoundException
import org.damascus.logic.model.Project
import org.damascus.logic.model.ProjectState
import org.damascus.logic.model.Task
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskRepository
import org.damascus.logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class GetProjectStateUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskStateRepository: TaskStateRepository
    private lateinit var getProjectStateUseCase: GetProjectStateUseCase

    private val taskStatesList = listOf(
        TaskState(UUID.randomUUID(), "TODO", 1),
        TaskState(UUID.randomUUID(), "DONE", 1),
        TaskState(UUID.randomUUID(), "In Progress", 1)
    )

    @BeforeEach
    fun setup() {
        taskRepository = mockk(relaxed = true)
        taskStateRepository = mockk(relaxed = true)
        getProjectStateUseCase = GetProjectStateUseCase(taskRepository, taskStateRepository)
    }

    @Test
    fun `should throw NoTasksFoundException when project has no tasks`() {
        // Given
        every { taskRepository.getByProject(any()) } returns emptyList()

        // When && Then
        assertThrows<NoTasksFoundException> {
            getProjectStateUseCase(UUID.randomUUID())
        }
    }

    @Test
    fun `should return valid ProjectState when project has tasks`() {
        // Given
        val project = getProject()
        every { taskRepository.getByProject(project.id) } returns getListOfProjectTasks(project.id)
        every { taskStateRepository.getAllStates() } returns taskStatesList

        // When
        val resultProjectState = getProjectStateUseCase(project.id)

        // Then
        assertThat(resultProjectState).isEqualTo(
            ProjectState(
                mapOf(
                    taskStatesList[0] to 1,
                    taskStatesList[1] to 1,
                    taskStatesList[2] to 1,
                )
            )
        )

    }

    private fun getProject(id: UUID = UUID.randomUUID()): Project {
        return Project(
            id = id,
            name = "Dummy",
            assignedMatesIds = mutableListOf(),
            allowedStatesIds = mutableListOf(),
            creationDate = LocalDateTime(2023, 10, 7, 3, 30, 0)
        )
    }

    private fun getListOfProjectTasks(projectId: UUID): List<Task> = listOf(
        getTask(projectId, taskStatesList[0].id),
        getTask(projectId, taskStatesList[1].id),
        getTask(projectId, taskStatesList[2].id)
    )

    private fun getTask(projectId: UUID, stateId: UUID): Task {
        return Task(
            id = UUID.randomUUID(),
            projectId = projectId,
            title = "Hard task",
            description = "A nightmare",
            assigneeId = UUID.randomUUID(),
            stateId = stateId,
            creationDate = LocalDateTime.parse("2024-05-01T12:00:00")
        )
    }

}