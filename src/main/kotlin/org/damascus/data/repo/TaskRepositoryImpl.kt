package org.damascus.data.repo

import org.damascus.data.dto.TaskDTO
import org.damascus.data.mapper.toDto
import org.damascus.data.mapper.toModel
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
    private val taskDataSource: DataSource<TaskDTO>
) : TaskRepository {

    override suspend fun create(task: Task) {
        if (exists(task.id)) throw TaskAlreadyExistsException(task.id)
        taskDataSource.write(task.toDto())
    }

    override suspend fun update(taskId: UUID, task: Task) {
        if (exists(taskId).not()) throw TaskNotFoundException(task.id)
        taskDataSource.update(taskId, task.toDto())
    }

    override suspend fun delete(taskId: UUID) {
        if (exists(taskId).not()) throw TaskNotFoundException(taskId)
        taskDataSource.delete(taskId)
    }

    override suspend fun get(taskId: UUID): Task {
        return taskDataSource.read()
            .firstOrNull { it.id == taskId }?.toModel()
            ?: throw TaskNotFoundException(taskId)
    }

    override suspend fun getByProject(projectId: UUID): List<Task> =
        taskDataSource.read().filter { it.projectId == projectId }.map { it.toModel() }

    private suspend fun exists(taskId: UUID): Boolean =
        taskDataSource.read().any { it.id == taskId }
}