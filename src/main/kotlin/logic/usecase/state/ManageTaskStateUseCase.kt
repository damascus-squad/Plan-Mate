package org.damascus.logic.usecase.state

import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import java.util.*

class ManageTaskStateUseCase(
    private val taskStateRepository: TaskStateRepository
) {
    fun createTaskState(taskStateName: String): Boolean {
        return taskStateRepository.create(taskStateName)
    }

    fun getTaskState(id: UUID): TaskState {
        return taskStateRepository.getTaskStateById(id)
    }

    fun getAllTaskStates(): List<TaskState> {
        return taskStateRepository.getAllStates()
    }

    fun updateTaskState(taskState: TaskState, updatedTaskState: TaskState): Boolean {
        if (!taskStateRepository.exist(updatedTaskState.name)) {
            throw StateNotFoundException()
        }
        return taskStateRepository.update(taskState, updatedTaskState)
    }

    fun deleteTaskState(taskState: TaskState): Boolean {
        if (!taskStateRepository.exist(taskState.name)) {
            throw StateNotFoundException()
        }
        return taskStateRepository.delete(taskState)
    }
}

