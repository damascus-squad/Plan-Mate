package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.ProjectNotFoundException
import logic.model.ActionType
import logic.model.History
import logic.model.Project
import logic.model.User
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.DeleteProjectUseCase
import ui.io.Display
import ui.io.InputReader
import java.util.*

class DeleteProjectUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val saveLogUseCase: SaveLogUseCase
) {
    operator fun invoke(admin: User, currentProject: Project) {
        try {
            val confirm = inputReader.readBoolean(prompt = "Are you sure you want to delete this Project? (yes/no)")
            if (confirm) {
                if (deleteProjectUseCase(currentProject.id)) {
                    saveLogUseCase(
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