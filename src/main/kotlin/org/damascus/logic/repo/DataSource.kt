package org.damascus.logic.repo

import java.util.*

interface DataSource<T> {
    suspend fun read(): List<T>
    suspend fun write(entry: T)
    suspend fun write(entriesList: List<T>)
    suspend fun update(id: UUID, updatedData: T)
    suspend fun delete(id: UUID)

}