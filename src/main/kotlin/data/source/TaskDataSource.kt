package org.damascus.data.source


interface TaskDataSource <T> {
    fun save(data: List<T>): Boolean
    fun load(): List<T>
}