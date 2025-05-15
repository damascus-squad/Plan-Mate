package org.damascus.logic.repo

import org.damascus.logic.model.TaskState
import java.util.*

interface TaskStateRepository {
    suspend fun getAllStates(): List<TaskState>
    suspend fun getTaskStateById(id: UUID): TaskState
    suspend fun create(taskStateName: String): TaskState
    suspend fun update(taskState: TaskState, updatedTaskState: TaskState): Boolean
    suspend fun delete(taskState: TaskState): Boolean
    suspend fun exists(name: String): Boolean
    suspend fun incrementProjectReferences(taskState: TaskState): Boolean
}