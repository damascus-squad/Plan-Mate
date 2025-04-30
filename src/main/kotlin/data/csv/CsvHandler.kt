package org.damascus.data.csv

interface CsvHandler<T> {
    fun read(): List<T>
    fun write(data: List<T>)
    fun update(id: String, updatedData: T)
    fun delete(id: String)
}
