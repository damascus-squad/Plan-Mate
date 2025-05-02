package org.damascus.data.repo

import org.damascus.logic.model.State
import org.damascus.data.source.StateDataSource
import org.damascus.logic.StateRepository
import org.damascus.logic.exception.DuplicateStateException
import org.damascus.logic.exception.StateNotFoundException
import java.util.*

class StateRepositoryImpl(private val stateDataSource: StateDataSource<State>) : StateRepository {

    override fun getAllStates(): List<State> {
        return stateDataSource.read()
    }

    override fun getStateById(id: UUID): State? {
        return stateDataSource.read().firstOrNull { it.id == id }
    }

    override fun create(state: State): Boolean {
        if (exist(state.id)) {
            throw DuplicateStateException(state.id)
        }
        stateDataSource.write(state)

        return true
    }

    override fun update(state: State): Boolean {
        if (!exist(state.id)) {
            throw StateNotFoundException(state.id)
        }
        stateDataSource.update(state.id, state)

        return true
    }

    override fun delete(state: State): Boolean {
        if (!exist(state.id)) {
            throw StateNotFoundException(state.id)
        }
        stateDataSource.delete(state.id)

        return true
    }

    override fun exist(id: UUID): Boolean {
        return stateDataSource.read().any { it.id == id }
    }
}