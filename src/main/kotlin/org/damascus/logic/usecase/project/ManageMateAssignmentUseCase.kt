package org.damascus.logic.usecase.project

import org.damascus.logic.repo.ProjectRepository
import java.util.*

class ManageMateAssignmentUseCase(
    private val repository: ProjectRepository
) {
    fun assign(
        projectId: UUID,
        mateId: UUID,
    ) = manageAssignment(
        projectId = projectId,
        action = {
            if (mateId !in this)
                add(mateId)
        }
    )

    fun unAssign(
        projectId: UUID,
        mateId: UUID,
    ) = manageAssignment(
        projectId = projectId,
        action = {
            if (mateId in this)
                remove(mateId)
        }
    )

    private fun manageAssignment(
        projectId: UUID,
        action: MutableList<UUID>.() -> Unit
    ): Boolean {
        val project = try {
            repository.get(projectId)
        } catch (_: Exception) {
            return false
        }

        val updatedMates = project.assignedMatesIds.apply(action)
        val updatedProject = project.copy(assignedMatesIds = updatedMates)
        return repository.update(projectId, updatedProject)
    }
}
