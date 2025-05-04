package org.damascus.logic.usecase.ProjectUseCase

import logic.repo.ProjectRepository
import java.util.UUID

class CheckProjectExistsUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID): Boolean {
        return repository.exists(projectId)
    }
}
