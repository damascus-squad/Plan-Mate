package org.damascus.data.source

import java.util.*

interface StateDataSource<State> {
    fun read():List<State>
    fun write(state:State): Boolean
    fun update(id: UUID, updatedState: State): Boolean
    fun delete(id: UUID): Boolean

}