package logic.usecase.task

import logic.model.Task
import logic.repo.TaskRepository
import java.util.*

class GetTasksByProjectUseCase(private val repository: TaskRepository) {
    operator fun invoke(projectId: UUID): List<Task> = repository.getByProject(projectId)
}