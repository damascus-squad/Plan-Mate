package org.damascus.logic.usecase.project

import org.damascus.logic.exception.NoTasksFoundException
import org.damascus.logic.model.ProjectState
import org.damascus.logic.repo.TaskRepository
import org.damascus.logic.repo.TaskStateRepository
import org.koin.core.annotation.Single
import java.util.*

@Single
class GetProjectStateUseCase(
    private val taskRepository: TaskRepository,
    private val taskStateRepository: TaskStateRepository
) {
    operator fun invoke(projectId: UUID): ProjectState {
        val projectTaskStateIds = taskRepository.getByProject(projectId).map { it.stateId }
        if (projectTaskStateIds.isEmpty()) throw NoTasksFoundException("Project has no tasks, hence has no state!")

        val taskStateFrequency =
            taskStateRepository.getAllStates().filter { it.id in projectTaskStateIds }
                .groupingBy { it }.eachCount()

        return ProjectState(taskStateFrequency)
    }
}