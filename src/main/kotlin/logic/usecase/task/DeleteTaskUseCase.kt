package logic.usecase.task

import logic.repo.TaskRepository
import java.util.*

class DeleteTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(taskId: UUID) = repository.delete(taskId)
}