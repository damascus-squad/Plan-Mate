package org.damascus.logic.usecase.ProjectUseCase

import logic.model.Project
import org.damascus.logic.repository.ProjectRepository

class GetAllProjectsUseCase(private val repository: ProjectRepository) {
    operator fun invoke(): List<Project> {
        return repository.getAll()
    }
}