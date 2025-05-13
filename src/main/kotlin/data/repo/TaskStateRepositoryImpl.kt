package data.repo

import logic.exception.StateNotFoundException
import logic.model.History.Companion.NO_TASK_STATE
import logic.model.TaskState
import logic.repo.DataSource
import logic.repo.TaskStateRepository
import java.util.*

class TaskStateRepositoryImpl(private val dataSource: DataSource<TaskState>) : TaskStateRepository {

    override fun getAllStates(): List<TaskState> {
        return dataSource.read()
    }

    override fun getTaskStateById(id: UUID): TaskState {
        return dataSource.read().firstOrNull { it.id == id }
            ?: NO_TASK_STATE
    }

    override fun create(taskStateName: String): Boolean {
        dataSource.write(
            TaskState(
                id = UUID.randomUUID(),
                name = taskStateName,
                projectReferencesCount = 1
            )
        )

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

        if (taskState.projectReferencesCount > 1) {
            update(
                taskState = taskState,
                updatedTaskState = taskState.copy(projectReferencesCount = taskState.projectReferencesCount - 1)
            )

            return true
        }

        dataSource.delete(taskState.id)

        return true
    }

    override fun incrementProjectReferences(taskState: TaskState): Boolean {
        if (!exist(taskState.name)) {
            throw StateNotFoundException()
        }

        update(
            taskState = taskState,
            updatedTaskState = taskState.copy(projectReferencesCount = taskState.projectReferencesCount + 1)
        )

        return true
    }

    override fun exist(name: String): Boolean {
        return dataSource.read().any { it.name == name }
    }
}