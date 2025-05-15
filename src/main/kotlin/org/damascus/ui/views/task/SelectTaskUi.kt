package org.damascus.ui.views.task

import org.damascus.logic.model.Task
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.koin.core.annotation.Single

@Single
class SelectTaskUi(
    private val display: Display,
    private val inputReader: InputReader,
    private val manageTaskUseCase: ManageTaskUseCase,
) {
    operator fun invoke(tasks: List<Task>): Task {

        val selection = inputReader.readInt(
            prompt = "Enter task number to take action on",
            min = 1,
            max = tasks.size
        )

        val selectedTask = manageTaskUseCase.getTask(tasks[selection - 1].id)
        display.write(prompt = "You selected: ${selectedTask.title}")
        return selectedTask
    }
}