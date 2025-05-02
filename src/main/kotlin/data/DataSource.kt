package org.damascus.data

import java.util.UUID

interface DataSource<T> {
    fun read(): List<T>
    fun write(entry: T)
    fun write(entriesList: List<T>)
    fun update(id: UUID, updatedData: T)
    fun delete(id: UUID)
}