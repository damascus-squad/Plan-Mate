package org.damascus.data.repo

import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.State
import logic.repo.DataSource
import logic.repo.TaskStateRepository
import java.util.*

class TaskStateRepositoryImpl(private val dataSource: DataSource<State>) : TaskStateRepository {

    override fun getAllStates(): List<State> {
        return dataSource.read()
    }

    override fun getStateById(id: UUID): State? {
        return dataSource.read().firstOrNull { it.id == id }
    }

    override fun create(state: State): Boolean {
        if (exist(state.id)) {
            throw DuplicateStateException(state.id)
        }
        dataSource.write(state)

        return true
    }

    override fun update(state: State): Boolean {
        if (!exist(state.id)) {
            throw StateNotFoundException(state.id)
        }
        dataSource.update(state.id, state)

        return true
    }

    override fun delete(state: State): Boolean {
        if (!exist(state.id)) {
            throw StateNotFoundException(state.id)
        }
        dataSource.delete(state.id)

        return true
    }

    override fun exist(id: UUID): Boolean {
        return dataSource.read().any { it.id == id }
    }
}