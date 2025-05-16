package org.damascus.logic.usecase.task

import org.damascus.logic.model.Task
import org.damascus.logic.repo.TaskRepository
import org.koin.core.annotation.Single
import java.util.*

@Single
class ManageTaskUseCase(
    private val taskRepo: TaskRepository
) {
    fun createTask(task: Task) = taskRepo.create(task)
    fun getProjectTasks(projectId: UUID) = taskRepo.getByProject(projectId)
    fun getTask(taskId: UUID) = taskRepo.get(taskId)
    fun updateTask(taskId: UUID, task: Task) = taskRepo.update(taskId, task)
    fun deleteTask(taskId: UUID) = taskRepo.delete(taskId)
}