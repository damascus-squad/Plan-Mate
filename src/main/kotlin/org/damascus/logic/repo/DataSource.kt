package org.damascus.logic.repo

import java.util.*

interface DataSource<T> {
    fun read(): List<T>
    fun write(entry: T)
    fun write(entriesList: List<T>)
    fun update(id: UUID, updatedData: T)
    fun delete(id: UUID)
}