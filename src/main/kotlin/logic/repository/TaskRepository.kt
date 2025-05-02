package logic.repository

import logic.model.Task
import java.util.UUID

interface TaskRepository {
    fun create(task: Task)
    fun update(taskId: UUID, task: Task)
    fun delete(taskId: UUID)
    fun get(taskId: UUID): Task
    fun getByProject(projectId: UUID): List<Task>
}