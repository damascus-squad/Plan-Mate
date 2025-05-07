package logic.usecase.task

import logic.model.Task
import logic.repo.TaskRepository
import java.util.*

class UpdateTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(taskId: UUID, task: Task) = repository.update(taskId, task)
}