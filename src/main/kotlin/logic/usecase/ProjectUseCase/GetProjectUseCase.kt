package org.damascus.logic.usecase.ProjectUseCase

import logic.model.Project
import org.damascus.logic.repository.ProjectRepository
import java.util.UUID

class GetProjectUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID): Project {
        return repository.get(projectId)
    }
}
