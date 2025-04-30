package org.damascus.data.repo


import model.*
import org.damascus.data.source.TaskDataSource
import org.damascus.logic.repository.TaskRepository
import java.util.*

class TaskRepositoryImpl(private val taskDataSource: TaskDataSource<Task>) : TaskRepository {

    override fun create(task: Task): Boolean {
        if (exists(task.id)) return false
        return taskDataSource.save(taskDataSource.load() + task)
    }

    override fun update(taskId: UUID, task: Task): Boolean {
        val updatedTasks = taskDataSource.load().map {
            if (it.id == taskId) task else it
        }

        val isUpdated = updatedTasks.any { it.id == taskId }
        return if (isUpdated) taskDataSource.save(updatedTasks) else false
    }

    override fun delete(taskId: UUID): Boolean {
        val currentTasks = taskDataSource.load()
        val remainingTasks = currentTasks.filterNot { it.id == taskId }

        if (remainingTasks.size == currentTasks.size) return false // No deletion occurred
        return taskDataSource.save(remainingTasks)
    }

    override fun exists(taskId: UUID): Boolean {
        return taskDataSource.load().any { it.id == taskId }
    }

    override fun get(taskId: UUID): Task {
        return taskDataSource.load()
            .firstOrNull { it.id == taskId }
            ?: throw NoSuchElementException("Task with ID $taskId not found.")
    }

    override fun getByProject(projectId: UUID): List<Task> {
        return taskDataSource.load().filter { it.projectId == projectId }
    }
}