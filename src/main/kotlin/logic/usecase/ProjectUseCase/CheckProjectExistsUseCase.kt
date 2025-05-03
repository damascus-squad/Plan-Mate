package org.damascus.logic.usecase.ProjectUseCase

import org.damascus.logic.repository.ProjectRepository
import java.util.UUID

class CheckProjectExistsUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID): Boolean {
        return repository.exists(projectId)
    }
}
