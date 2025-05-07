package logic.usecase.task

import logic.model.Task
import logic.repo.TaskRepository

class CreateTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(task: Task) = repository.create(task)
}
