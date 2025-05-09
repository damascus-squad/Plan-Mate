package org.damascus.ui.views.task

import logic.model.TaskState
import logic.usecase.task.GetTasksByProjectUseCase
import ui.util.printTable
import java.util.*

class GetAllTasksByProjectIdUi (
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase
){
    operator fun invoke(allowedStates: List<TaskState>, projectId: UUID) {
        val projectTasks = getTasksByProjectUseCase(projectId)

        val groupedTasks = allowedStates.map { state ->
            state.name to projectTasks.filter { it.stateId == state.id }
        }

        val maxRows = groupedTasks.maxOf { it.second.size }

        val tableRows = (0 until maxRows).map { rowIndex ->
            groupedTasks.map { (_, tasks) ->
                tasks.getOrNull(rowIndex)?.title ?: ""
            }
        }

        val headers = allowedStates.map { it.name }
        printTable(headers, tableRows)
    }
}