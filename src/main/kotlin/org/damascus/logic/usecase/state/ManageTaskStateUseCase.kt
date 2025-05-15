package org.damascus.logic.usecase.state

import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskStateRepository
import java.util.*

class ManageTaskStateUseCase(
    private val taskStateRepo: TaskStateRepository
) {
    suspend fun createTaskState(taskStateName: String) = taskStateRepo.create(taskStateName)
    suspend fun getTaskState(id: UUID) = taskStateRepo.getTaskStateById(id)
    suspend fun getAllTaskStates() = taskStateRepo.getAllStates()

    suspend fun updateTaskState(taskState: TaskState, updatedTaskState: TaskState): Boolean {
        if (taskStateRepo.exists(updatedTaskState.name)) {
            return taskStateRepo.update(taskState, updatedTaskState)
        }

        throw StateNotFoundException()
    }

    suspend fun deleteTaskState(taskState: TaskState): Boolean {
        if (taskStateRepo.exists(taskState.name)) {
            return taskStateRepo.delete(taskState)
        }

        throw StateNotFoundException()
    }
}

