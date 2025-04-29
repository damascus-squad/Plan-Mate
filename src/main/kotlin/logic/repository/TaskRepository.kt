package org.damascus.logic.repository

import org.damascus.logic.model.Task
import java.util.UUID

interface TaskRepository {
    fun createTask(task: Task) : Boolean
    fun updateTask(taskId: UUID, task: Task): Boolean
    fun deleteTask(taskId: UUID): Boolean
    fun taskExists(taskId: UUID): Boolean
    fun getTask(taskId: UUID): Task
    fun getTasksByProject(projectId: UUID): List<Task>
}