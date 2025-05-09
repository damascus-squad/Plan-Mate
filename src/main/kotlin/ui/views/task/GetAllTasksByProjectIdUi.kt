package org.damascus.ui.views.task

import logic.model.Project
import logic.model.TaskState
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.GetTasksByProjectUseCase
import ui.util.printTable

class GetAllTasksByProjectIdUi (
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getTaskStateUseCase: GetTaskStateByIdUseCase
){
    operator fun invoke(currentProject: Project) {
        val projectTasks = getTasksByProjectUseCase(currentProject.id)

        val allowedStates: MutableList<TaskState> = mutableListOf()
        projectTasks.forEach {task->
             allowedStates.add(getTaskStateUseCase(task.id))
        }

        val groupedTasks = allowedStates.map { state ->
            state.name to projectTasks.filter { it.stateId == state.id }
        }

        val maxRows = groupedTasks.maxOfOrNull { it.second.size } ?: 0

        val tableRows = (0 until maxRows).map { rowIndex ->
            groupedTasks.map { (_, tasks) ->
                tasks.getOrNull(rowIndex)?.title ?: ""
            }
        }

        val headers = allowedStates.map { it.name }
        printTable(headers, tableRows)
    }
}