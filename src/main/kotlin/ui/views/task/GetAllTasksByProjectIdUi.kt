package org.damascus.ui.views.task

import logic.model.Project
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.GetTasksByProjectUseCase
import ui.io.Display
import ui.util.printTable

class GetAllTasksByProjectIdUi(
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getTaskStateUseCase: GetTaskStateByIdUseCase,
    private val display: Display
) {
    operator fun invoke(currentProject: Project): Boolean {
        val projectTasks = getTasksByProjectUseCase(currentProject.id)

        if (projectTasks.isEmpty()) {
            display.writeError(errorMessage = "No Tasks Found. Please Create Task First")
            return false
        } else {
            val allowedStates = currentProject.allowedStatesIds.map { getTaskStateUseCase(it) }

            val groupedTasksMap = projectTasks.groupBy { it.stateId }

            val groupedTasks = allowedStates.map { state ->
                state.name to (groupedTasksMap[state.id] ?: emptyList())
            }

            val maxRows = groupedTasks.maxOfOrNull { it.second.size } ?: 0

            var globalCounter = 1 // ✅ Start the global counter here

            val tableRows = (0 until maxRows).map { rowIndex ->
                groupedTasks.map { (_, tasks) ->
                    val task = tasks.getOrNull(rowIndex)
                    if (task != null) {
                        "${globalCounter++}. ${task.title}"
                    } else {
                        ""
                    }
                }
            }

            val headers = allowedStates.map { it.name }

            printTable(headers, tableRows)
            return true
        }
    }
}