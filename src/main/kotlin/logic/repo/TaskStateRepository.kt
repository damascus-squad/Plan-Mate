package logic.repo

import logic.model.TaskState
import java.util.*

interface TaskStateRepository {
    fun getAllStates(): List<TaskState>
    fun getTaskStateById(id: UUID): TaskState?
    fun create(taskState: TaskState): Boolean
    fun update(taskState: TaskState, updatedTaskState: TaskState): Boolean
    fun delete(taskState: TaskState): Boolean
    fun exist(name: String): Boolean
}