package org.damascus.logic.repo

import org.damascus.logic.model.Task
import java.util.*

interface TaskRepository {
    suspend fun create(task: Task)
    suspend fun update(taskId: UUID, task: Task)
    suspend fun delete(taskId: UUID)
    suspend fun get(taskId: UUID): Task
    suspend fun getByProject(projectId: UUID): List<Task>
}