package org.damascus.logic.usecase.ProjectUseCase

import logic.model.Project
import org.damascus.logic.repository.ProjectRepository
import java.util.UUID

class UpdateProjectUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID, project: Project): Boolean {
        return repository.update(projectId, project)
    }
}