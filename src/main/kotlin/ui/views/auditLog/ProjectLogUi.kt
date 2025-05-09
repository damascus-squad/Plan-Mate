package org.damascus.ui.views.auditLog

import logic.exception.NoLogException
import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.project.GetProjectUseCase
import ui.io.Display
import java.util.*

class ProjectLogUi (
    private val display: Display,
    private val getProjectUseCase: GetProjectUseCase,
    private val getLogsByProjectId: GetLogsByProjectIdUseCase
){
    operator fun invoke(projectId: UUID) {
        val projectName = getProjectUseCase(projectId).name

        try {
            val log = getLogsByProjectId(projectId)
            display.write("📄 Log for Project Name [$projectName]:\n$log")
        } catch (e: NoLogException) {
            display.writeError(errorMessage = "No log found for Project Name: $projectName")
        }
    }
}