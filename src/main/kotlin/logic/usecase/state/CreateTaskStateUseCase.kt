package logic.usecase.state

import logic.exception.DuplicateStateException
import logic.model.TaskState
import logic.repo.TaskStateRepository

class CreateTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState): Boolean {
        if (repository.exist(taskState.name)) {
            throw DuplicateStateException(taskState.name)
        }
        return repository.create(taskState)
    }
}