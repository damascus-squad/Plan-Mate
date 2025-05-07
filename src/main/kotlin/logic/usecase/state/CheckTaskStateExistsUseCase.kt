package logic.usecase.state

import logic.repo.TaskStateRepository

class CheckTaskStateExistsUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(name: String): Boolean {
        return repository.exist(name)
    }
}
