package org.damascus.logic.usecase.state

import logic.exception.DuplicateStateException
import logic.model.TaskState
import logic.repo.TaskStateRepository

class CreateTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState): Boolean {
        if (repository.exist(taskState.id)) {
            throw DuplicateStateException(taskState.id)
        }
        return repository.create(taskState)
    }
}