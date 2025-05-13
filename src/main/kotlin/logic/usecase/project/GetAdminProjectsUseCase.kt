package logic.usecase.project

import logic.model.Project
import logic.repo.ProjectRepository

class GetAdminProjectsUseCase(private val repository: ProjectRepository) {
    operator fun invoke(): List<Project> {
        return repository.getAll()
    }
}