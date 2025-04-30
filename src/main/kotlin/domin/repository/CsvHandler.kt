package org.damascus.domin.repository

interface CsvHandler<T> {
    fun read(filePath: String): List<T>
    fun write(filePath: String, data: List<T>)
    fun update(filePath: String, id: String, updatedData: T)
    fun delete(filePath: String, id: String)
}
