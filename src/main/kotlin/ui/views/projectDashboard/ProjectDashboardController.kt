package org.damascus.ui.views.projectDashboard

import logic.model.Project
import logic.model.Task
import java.util.UUID

interface ProjectDashboardController {
    fun editProject(projectId: UUID)
    fun deleteTask(taskId: UUID)
    fun createTask(projectId: UUID,task: Task)
    fun assignMateToProject(
        projectId: UUID,
        mateId: UUID,
        shouldAssign: Boolean
    )
    fun viewProjectHistory(projectId: UUID)
    fun viewTaskHistory(projectId: UUID)

}