package logic.usecase.state

import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository

class DeleteTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState): Boolean {
        if (!repository.exist(taskState.name)) {
            throw StateNotFoundException()
        }
        return repository.delete(taskState)
    }
}