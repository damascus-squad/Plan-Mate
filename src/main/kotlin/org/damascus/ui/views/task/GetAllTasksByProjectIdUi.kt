package org.damascus.ui.views.task

import org.damascus.logic.model.Project
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.printTable

class GetAllTasksByProjectIdUi(
    private val display: Display,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {
    operator fun invoke(currentProject: Project): Boolean {
        val projectTasks = manageTaskUseCase.getProjectTasks(currentProject.id)

        if (projectTasks.isEmpty()) {
            display.writeError(errorMessage = "No Tasks Found. Please Create Task First")
            return false
        } else {
            val allowedStates = currentProject.allowedStatesIds.map { manageTaskStateUseCase.getTaskState(it) }

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