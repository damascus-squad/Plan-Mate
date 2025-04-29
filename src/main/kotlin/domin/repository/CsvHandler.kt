package org.damascus.domin.repository

interface CsvHandler<T> {
    fun read(filePath: String): List<T>
    fun write(filePath: String, data: List<T>)
}
