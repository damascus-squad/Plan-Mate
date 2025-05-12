package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.model.Project
import logic.model.User
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.UpdateProjectUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.printProjectDetails
import java.util.*

class UpdateProjectTitleUi(
    private val inputReader: InputReader,
    private val saveLogUseCase: SaveLogUseCase,
    private val display: Display,
    private val updateProjectUseCase: UpdateProjectUseCase
) {
    operator fun invoke (currentProject: Project, currentUser: User) {
        val newTitle = inputReader.readString("Enter new title (or type 's' to keep current)")

        if (newTitle.lowercase() != "s") {
            val updatedProject = currentProject.copy(name = newTitle)
            updateProjectUseCase(projectId = updatedProject.id, updatedProject)

            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = History.NO_UUID,
                    actionType = ActionType.PROJECT_MODIFIED,
                    userId = currentUser.id,
                    currentStateId = History.NO_UUID,
                    newStateId = History.NO_UUID,
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
