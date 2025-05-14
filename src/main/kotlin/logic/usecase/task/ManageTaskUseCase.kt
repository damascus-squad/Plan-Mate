package org.damascus.logic.usecase.task

import logic.model.Task
import logic.repo.TaskRepository
import java.util.*

class ManageTaskUseCase(
    private val taskRepo: TaskRepository
) {
    fun createTask(task: Task) = taskRepo.create(task)
    fun getProjectTasks(projectId: UUID) = taskRepo.getByProject(projectId)
    fun getTask(taskId: UUID) = taskRepo.get(taskId)
    fun updateTask(taskId: UUID, task: Task) = taskRepo.update(taskId, task)
    fun deleteTask(taskId: UUID) = taskRepo.delete(taskId)
}