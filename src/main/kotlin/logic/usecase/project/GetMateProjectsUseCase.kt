package logic.usecase.project

import logic.model.Project
import logic.repo.ProjectRepository
import java.util.*

class GetMateProjectsUseCase(private val repository: ProjectRepository) {
    operator fun invoke(mateId: UUID): List<Project> {
        return repository.getAllProjectsByMateId(mateId)
    }
}