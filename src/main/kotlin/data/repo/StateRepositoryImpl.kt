package org.damascus.data.repo

import logic.model.State
import org.damascus.data.DataSource
import org.damascus.data.source.StateDataSource
import org.damascus.logic.StateRepository
import org.damascus.logic.exception.DuplicateStateException
import org.damascus.logic.exception.StateNotFoundException
import java.util.*

class StateRepositoryImpl(private val dataSource: DataSource<State>) : StateRepository {

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