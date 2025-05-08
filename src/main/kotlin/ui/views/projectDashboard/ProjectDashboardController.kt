package org.damascus.ui.views.projectDashboard

import logic.model.User
import java.util.*

interface ProjectDashboardController {
    fun editProject(projectId: UUID)
    fun deleteTask(taskId: UUID)
    fun createTask(projectId: UUID, currentUser: User)
    fun assignMateToProject(
        projectId: UUID,
        mateId: UUID,
    )
    fun start(projectId: UUID, currentUser: User)
}