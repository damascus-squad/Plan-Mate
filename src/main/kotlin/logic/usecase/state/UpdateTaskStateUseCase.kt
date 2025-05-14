package logic.usecase.state

import logic.exception.DuplicateStateException
import logic.model.TaskState
import logic.repo.TaskStateRepository

// beta version
class UpdateTaskStateUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(taskState: TaskState, updatedTaskState: TaskState): Boolean {
        if (taskState.name != updatedTaskState.name && repository.exist(updatedTaskState.name)) {
            throw DuplicateStateException(updatedTaskState.name)
        }
        return repository.update(taskState, updatedTaskState)
    }
}
// old version

//class UpdateTaskStateUseCase(
//    private val repository: TaskStateRepository
//) {
//    operator fun invoke(taskState: TaskState, updatedTaskState: TaskState): Boolean {
//        if (!repository.exist(updatedTaskState.name)) {
//            throw StateNotFoundException()
//        }
//        return repository.update(taskState, updatedTaskState)
//    }
//}
