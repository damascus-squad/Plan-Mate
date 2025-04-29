package org.damascus

import org.damascus.logic.model.Task
import org.damascus.logic.repository.TaskRepository
import java.util.UUID

class TaskHelper(private val taskRepository: TaskRepository) {
    fun createTask(task: Task): Boolean {
        return taskRepository.createTask(task)
    }

    fun updateTask(taskId: UUID, task: Task): Boolean {
       return taskRepository.updateTask(taskId, task)
    }

    fun deleteTask(taskId: UUID): Boolean {
        return taskRepository.deleteTask(taskId)
    }

    fun taskExist(taskId: UUID): Boolean {
        return taskRepository.taskExists(taskId)
    }

    fun getTask(taskId: UUID): Task {
        return taskRepository.getTask(taskId)
    }

    fun getTasksByProject(projectId: UUID): List<Task> {
        return taskRepository.getTasksByProject(projectId)
    }

}