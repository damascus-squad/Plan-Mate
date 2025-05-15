package org.damascus.data.repo

import org.damascus.logic.exception.TaskAlreadyExistsException
import org.damascus.logic.exception.TaskNotFoundException
import org.damascus.logic.model.Task
import org.damascus.logic.repo.DataSource
import org.damascus.logic.repo.TaskRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

@Single
class TaskRepositoryImpl(
    @Named("taskDataSource")
    private val taskDataSource: DataSource<Task>
) : TaskRepository {

    override fun create(task: Task) {
        if (exists(task.id)) throw TaskAlreadyExistsException(task.id)
        taskDataSource.write(task)
    }

    override fun update(taskId: UUID, task: Task) {
        if (exists(taskId).not()) throw TaskNotFoundException(task.id)
        taskDataSource.update(taskId, task)
    }

    override fun delete(taskId: UUID) {
        if (exists(taskId).not()) throw TaskNotFoundException(taskId)
        taskDataSource.delete(taskId)
    }

    override fun get(taskId: UUID): Task {
        return taskDataSource.read()
            .firstOrNull { it.id == taskId }
            ?: throw TaskNotFoundException(taskId)
    }

    override fun getByProject(projectId: UUID): List<Task> =
        taskDataSource.read().filter { it.projectId == projectId }

    private fun exists(taskId: UUID): Boolean =
        taskDataSource.read().any { it.id == taskId }
}