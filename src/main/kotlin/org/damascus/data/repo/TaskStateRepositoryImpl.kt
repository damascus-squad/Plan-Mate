package org.damascus.data.repo

import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.model.History
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.DataSource
import org.damascus.logic.repo.TaskStateRepository
import java.util.*

class TaskStateRepositoryImpl(private val dataSource: DataSource<TaskState>) : TaskStateRepository {

    override suspend fun getAllStates(): List<TaskState> {
        return dataSource.read()
    }

    override suspend fun getTaskStateById(id: UUID): TaskState {
        return dataSource.read().firstOrNull { it.id == id }
            ?: History.Companion.NO_TASK_STATE
    }

    override suspend fun create(taskStateName: String): TaskState {
        if (exists(taskStateName)) {
            val existingTaskState = getTaskStateByName(taskStateName)
            incrementProjectReferences(existingTaskState)
            return existingTaskState
        }

        val newTaskState = TaskState(UUID.randomUUID(), taskStateName, 1)
        dataSource.write(newTaskState)
        return newTaskState
    }

    override suspend fun update(taskState: TaskState, updatedTaskState: TaskState): Boolean {
        if (!exists(taskState.name)) {
            throw StateNotFoundException()
        }
        dataSource.update(taskState.id, updatedTaskState)

        return true
    }

    override suspend fun delete(taskState: TaskState): Boolean {
        if (!exists(taskState.name)) {
            throw StateNotFoundException()
        }

        if (taskState.projectReferencesCount > 1) {
            update(
                taskState = taskState,
                updatedTaskState = taskState.copy(projectReferencesCount = taskState.projectReferencesCount - 1)
            )

            return true
        }

        dataSource.delete(taskState.id)

        return true
    }

    override suspend fun incrementProjectReferences(taskState: TaskState): Boolean {
        if (!exists(taskState.name)) {
            throw StateNotFoundException()
        }

        update(
            taskState = taskState,
            updatedTaskState = taskState.copy(projectReferencesCount = taskState.projectReferencesCount + 1)
        )

        return true
    }

    override suspend fun exists(name: String): Boolean {
        return dataSource.read().any { it.name == name }
    }

    private suspend fun getTaskStateByName(name: String): TaskState {
        return dataSource.read().firstOrNull { it.name == name } ?: History.Companion.NO_TASK_STATE
    }
}