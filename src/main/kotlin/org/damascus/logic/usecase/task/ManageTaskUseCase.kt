package org.damascus.logic.usecase.task

import org.damascus.logic.model.Task
import org.damascus.logic.repo.TaskRepository
import java.util.*

class ManageTaskUseCase(
    private val taskRepo: TaskRepository
) {
    suspend fun createTask(task: Task) = taskRepo.create(task)
    suspend fun getProjectTasks(projectId: UUID) = taskRepo.getByProject(projectId)
    suspend fun getTask(taskId: UUID) = taskRepo.get(taskId)
    suspend fun updateTask(taskId: UUID, task: Task) = taskRepo.update(taskId, task)
    suspend fun deleteTask(taskId: UUID) = taskRepo.delete(taskId)
}