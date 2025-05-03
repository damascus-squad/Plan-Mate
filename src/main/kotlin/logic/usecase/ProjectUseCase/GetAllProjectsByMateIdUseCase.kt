package org.damascus.logic.usecase.ProjectUseCase

import logic.model.Project
import org.damascus.logic.repository.ProjectRepository
import java.util.UUID

class GetAllProjectsByMateIdUseCase(private val repository: ProjectRepository) {
    operator fun invoke(mateId: UUID): List<Project> {
        return repository.getAllProjectsByMateId(mateId)
    }
}