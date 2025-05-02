package org.damascus.logic

import org.damascus.logic.model.State
import java.util.*

interface StateRepository {
    fun getAllStates():List<State>
    fun getStateById(id: UUID): State?
    fun create(state: State):Boolean
    fun update (state: State):Boolean
    fun delete (state: State):Boolean
    fun exist(id: UUID): Boolean
}