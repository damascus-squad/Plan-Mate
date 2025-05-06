package logic.usecase.project

import logic.repo.ProjectRepository
import java.util.*

class CheckProjectExistsUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID): Boolean {
        return repository.exists(projectId)
    }
}
