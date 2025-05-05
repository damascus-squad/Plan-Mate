package org.damascus.data.repo

import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.DataSource
import logic.repo.TaskStateRepository
import java.util.*

class TaskStateRepositoryImpl(private val dataSource: DataSource<TaskState>) : TaskStateRepository {

    override fun getAllStates(): List<TaskState> {
        return dataSource.read()
    }

    override fun getStateById(id: UUID): TaskState {
        return dataSource.read().firstOrNull { it.id == id }
            ?: throw StateNotFoundException(id)
    }

    override fun create(taskState: TaskState): Boolean {
        if (exist(taskState.id)) {
            throw DuplicateStateException(taskState.id)
        }
        dataSource.write(taskState)

        return true
    }

    override fun update(taskState: TaskState): Boolean {
        if (!exist(taskState.id)) {
            throw StateNotFoundException(taskState.id)
        }
        dataSource.update(taskState.id, taskState)

        return true
    }

    override fun delete(taskState: TaskState): Boolean {
        if (!exist(taskState.id)) {
            throw StateNotFoundException(taskState.id)
        }
        dataSource.delete(taskState.id)

        return true
    }

    override fun exist(id: UUID): Boolean {
        return dataSource.read().any { it.id == id }
    }
}