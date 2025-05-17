package org.damascus.data.repo

import org.damascus.data.dto.TaskStateDTO
import org.damascus.data.mapper.toDto
import org.damascus.data.mapper.toModel
import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.model.History
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.DataSource
import org.damascus.logic.repo.TaskStateRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

@Single
class TaskStateRepositoryImpl(
    @Named("taskStateDataSource")
    private val dataSource: DataSource<TaskStateDTO>
) : TaskStateRepository {

    override suspend fun getAllStates(): List<TaskState> {
        return dataSource.read().map { it.toModel() }
    }

    override suspend fun getTaskStateById(id: UUID): TaskState {
        return dataSource.read().firstOrNull { it.id == id }?.toModel()
            ?: History.NO_TASK_STATE
    }

    override suspend fun create(taskStateName: String): TaskState {
        if (exists(taskStateName)) {
            val existingTaskState = getTaskStateByName(taskStateName)
            incrementProjectReferences(existingTaskState)
            return existingTaskState
        }

        val newTaskState = TaskState(UUID.randomUUID(), taskStateName, 1)
        dataSource.write(newTaskState.toDto())
        return newTaskState
    }

    override suspend fun update(taskState: TaskState, updatedTaskState: TaskState): Boolean {
        if (!exists(taskState.name)) {
            throw StateNotFoundException()
        }
        dataSource.update(taskState.id, updatedTaskState.toDto())

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
        return dataSource.read().firstOrNull { it.name == name }?.toModel() ?: History.NO_TASK_STATE
    }
}