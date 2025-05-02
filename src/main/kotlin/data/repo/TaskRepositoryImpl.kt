package data.repo

import org.damascus.data.DataSource
import logic.TaskAlreadyExistsException
import logic.TaskNotFoundException
import logic.model.Task
import logic.repository.TaskRepository
import java.util.*

class TaskRepositoryImpl(
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