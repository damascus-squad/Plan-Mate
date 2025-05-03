package org.damascus.logic.usecase.task

import logic.model.Task
import logic.repo.TaskRepository
import java.util.*

class GetTaskUseCase (private val repository: TaskRepository) {
    operator fun invoke(taskId: UUID): Task = repository.get(taskId)
}