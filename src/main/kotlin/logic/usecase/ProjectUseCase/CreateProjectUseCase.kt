package org.damascus.logic.usecase.ProjectUseCase

import logic.model.Project
import org.damascus.logic.repository.ProjectRepository

class CreateProjectUseCase(private val repository: ProjectRepository) {
    operator fun invoke(project: Project): Boolean {
        if (repository.exists(project.id)) return false
        return repository.create(project)
    }
}