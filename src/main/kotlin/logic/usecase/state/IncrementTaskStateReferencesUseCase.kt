package logic.usecase.state

import logic.model.TaskState
import logic.repo.TaskStateRepository

class IncrementTaskStateReferencesUseCase(
    private val taskStateRepository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState) {
        taskStateRepository.incrementProjectReferences(taskState)
    }
}