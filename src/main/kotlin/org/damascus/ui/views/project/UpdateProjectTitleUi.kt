package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.util.printProjectDetails
import java.util.*

class UpdateProjectTitleUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase
) {
    operator suspend fun invoke (currentProject: Project, currentUser: User) {
        val newTitle = inputReader.readString("Enter new title (or type 's' to keep current)")

        if (newTitle.lowercase() != "s") {
            val updatedProject = currentProject.copy(name = newTitle)
            manageProjectUseCase.updateProject(projectId = updatedProject.id, updatedProject)

            manageAuditLogUseCase.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = History.Companion.NO_UUID,
                    actionType = ActionType.PROJECT_TITLE_MODIFIED,
                    userId = currentUser.id,
                    currentState = currentProject.name,
                    newState = updatedProject.name,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )

            display.write(prompt = "✅ Title updated successfully!" )
            updatedProject.printProjectDetails()
        } else {
            display.write(prompt = "ℹ️ Title unchanged.")
        }
    }
}
