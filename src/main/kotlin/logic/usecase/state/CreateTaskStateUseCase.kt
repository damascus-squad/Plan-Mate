package logic.usecase.state

import logic.model.TaskState
import logic.repo.TaskStateRepository

class CreateTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState): TaskState {
        return repository.create(taskState.name)
    }
}