package org.damascus.ui.views.task

import logic.model.Project
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.GetTasksByProjectUseCase
import ui.util.printTable

class GetAllTasksByProjectIdUi (
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getTaskStateUseCase: GetTaskStateByIdUseCase
){
    operator fun invoke(currentProject: Project) {
        val projectTasks = getTasksByProjectUseCase(currentProject.id)

        val allowedStates = currentProject.allowedStatesIds.map { getTaskStateUseCase(it) }

        val groupedTasksMap = projectTasks.groupBy { it.stateId }

        val groupedTasks = allowedStates.map { state ->
            state.name to (groupedTasksMap[state.id] ?: emptyList())
        }

        val maxRows = groupedTasks.maxOfOrNull { it.second.size } ?: 0

        val tableRows = (0 until maxRows).map { rowIndex ->
            groupedTasks.map { (_, tasks) ->
                val task = tasks.getOrNull(rowIndex)
                if (task != null) {
                    "${rowIndex + 1}. ${task.title}"
                } else {
                    ""
                }
            }
        }

        val headers = allowedStates.map { it.name }

        printTable(headers, tableRows)
    }
}