package org.damascus.logic.usecase.state

import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository

class UpdateTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState,updatedTaskState: TaskState): Boolean {
        if (!repository.exist(updatedTaskState.name)) {
            throw StateNotFoundException()
        }
        return repository.update(taskState,updatedTaskState)
    }
}