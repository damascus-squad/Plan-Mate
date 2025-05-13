package org.damascus.logic.usecase.task

import logic.model.Task
import logic.repo.TaskRepository
import java.util.UUID

class ManageTaskUseCase {
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(taskId: UUID, task: Task) = repository.update(taskId, task)
}

class GetTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(taskId: UUID): Task = repository.get(taskId)
}

class GetTasksByProjectUseCase(private val repository: TaskRepository) {
    operator fun invoke(projectId: UUID): List<Task> = repository.getByProject(projectId)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(taskId: UUID) = repository.delete(taskId)
}

class CreateTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(task: Task) = repository.create(task)
}
