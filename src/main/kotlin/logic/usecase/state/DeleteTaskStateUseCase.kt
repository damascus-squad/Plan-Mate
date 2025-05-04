package org.damascus.logic.usecase.state

import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository

class DeleteTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState): Boolean {
        if (!repository.exist(taskState.id)) {
            throw StateNotFoundException(taskState.id)
        }
        return repository.delete(taskState)
    }
}