package org.damascus.logic.usecase.state

import logic.repo.TaskStateRepository
import java.util.UUID

class CheckTaskStateExistsUseCase(
    private val repository: TaskStateRepository
) {
    operator fun invoke(id: UUID): Boolean {
        return repository.exist(id)
    }
}
