package org.damascus.logic.usecase.ProjectUseCase

import logic.repo.ProjectRepository
import java.util.UUID

class DeleteProjectUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID): Boolean {
        return repository.delete(projectId)
    }
}