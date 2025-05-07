package logic.usecase.state

import logic.model.TaskState
import logic.repo.TaskStateRepository

class GetAllTaskStatesUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(): List<TaskState> {
        return repository.getAllStates()
    }
}