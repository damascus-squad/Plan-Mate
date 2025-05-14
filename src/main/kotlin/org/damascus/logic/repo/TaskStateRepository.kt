package org.damascus.logic.repo

import org.damascus.logic.model.TaskState
import java.util.*

interface TaskStateRepository {
    fun getAllStates(): List<TaskState>
    fun getTaskStateById(id: UUID): TaskState
    fun create(taskStateName: String): TaskState
    fun update(taskState: TaskState, updatedTaskState: TaskState): Boolean
    fun delete(taskState: TaskState): Boolean
    fun exists(name: String): Boolean
    fun incrementProjectReferences(taskState: TaskState): Boolean
}