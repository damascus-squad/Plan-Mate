package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.exception.ProjectNotFoundException
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.koin.core.annotation.Single
import java.util.*

@Single
class DeleteProjectUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase
) {
    operator suspend fun invoke(admin: User, currentProject: Project) {
        try {
            val confirm = inputReader.readBoolean(prompt = "Are you sure you want to delete this Project? (yes/no)")
            if (confirm) {
                if (manageProjectUseCase.deleteProject(currentProject.id)) {
                    manageAuditLogUseCase.saveLog(
                        History(
                            id = UUID.randomUUID(),
                            projectId = currentProject.id,
                            taskId = History.NO_UUID,
                            actionType = ActionType.PROJECT_DELETED,
                            userId = admin.id,
                            currentState = null,
                            newState = null,
                            actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                    )
                    display.write(prompt = "🗑️ Project ${currentProject.id} deleted successfully!")
                }
            } else {
                display.writeError(errorMessage = "Project deletion canceled.")
            }
        } catch (e: ProjectNotFoundException) {
            display.writeError(errorMessage = "${e.message}")
        }
    }
}