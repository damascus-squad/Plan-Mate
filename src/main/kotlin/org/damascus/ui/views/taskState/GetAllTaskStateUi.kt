package org.damascus.ui.views.taskState

import org.damascus.logic.repo.TaskStateRepository
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.printTable
import org.damascus.ui.util.withStyle
import org.koin.core.annotation.Single

@Single
class GetAllTaskStateUi(
    private val taskStateRepository: TaskStateRepository,
){
    operator fun invoke() {
    val states = taskStateRepository.getAllStates()
    if (states.isEmpty()) {
        println("❗ No states found.".withStyle(TerminalColor.Red))
        return
    }

    val headers = listOf("State Name") + states.map { it.name }
    val usageRow = listOf("Used In") + states.map {
        val count = it.projectReferencesCount
        "$count project" + if (count != 1) "s" else ""
    }

    printTable(headers, listOf(usageRow), TerminalColor.Green)
}
}