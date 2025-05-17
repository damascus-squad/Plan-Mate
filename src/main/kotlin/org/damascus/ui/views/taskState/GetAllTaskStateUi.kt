package org.damascus.ui.views.taskState

import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskStateRepository
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.printTable
import org.koin.core.annotation.Single

@Single
class GetAllTaskStateUi(private val repository: TaskStateRepository) {
    operator fun invoke() {
        val states = repository.getAllStates()
        if (states.isEmpty()) {
            println("⚠️ No task states found.")
            return
        }
        val headers = buildHeaders(states)
        val usageRow = buildUsageRow(states)

        printTable(headers, usageRow)
    }

    fun buildHeaders(states: List<TaskState>): List<String> =
        listOf("State Name") + states.map { it.name }

    fun buildUsageRow(states: List<TaskState>): List<String> =
        listOf("Used In") + states.map {
            val count = it.projectReferencesCount
            "$count project" + if (count != 1) "s" else ""
        }

    private fun printTable(headers: List<String>, usageRow: List<String>) {
        printTable(headers, listOf(usageRow), TerminalColor.Green)
    }
}
