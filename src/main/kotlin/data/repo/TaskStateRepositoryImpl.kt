package data.repo

import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.DataSource
import logic.repo.TaskStateRepository
import java.util.*

class TaskStateRepositoryImpl(private val dataSource: DataSource<TaskState>) : TaskStateRepository {

    override fun getAllStates(): List<TaskState> {
        return dataSource.read()
    }

    override fun getStateById(id: UUID): TaskState {
        return dataSource.read().firstOrNull { it.id == id }
            ?: throw StateNotFoundException()
    }

    override fun create(taskState: TaskState): Boolean {
        if (exist(taskState.name)) {
            throw DuplicateStateException(taskState.name)
        }
        dataSource.write(taskState)

        return true
    }

    override fun update(taskState: TaskState, updatedTaskState: TaskState): Boolean {
        if (!exist(taskState.name)) {
            throw StateNotFoundException()
        }
        dataSource.update(taskState.id, updatedTaskState)

        return true
    }

    override fun delete(taskState: TaskState): Boolean {
        if (!exist(taskState.name)) {
            throw StateNotFoundException()
        }
        dataSource.delete(taskState.id)

        return true
    }

    override fun exist(name: String): Boolean {
        return dataSource.read().any { it.name == name }
    }
}