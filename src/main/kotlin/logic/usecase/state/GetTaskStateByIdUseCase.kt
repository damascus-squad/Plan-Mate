package logic.usecase.state

import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import java.util.*

class GetTaskStateByIdUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(id: UUID): TaskState {
        return repository.getTaskStateById(id)
            ?: throw StateNotFoundException()
    }
}
