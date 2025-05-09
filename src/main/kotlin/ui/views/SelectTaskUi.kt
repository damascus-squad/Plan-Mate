package org.damascus.ui.views

import logic.model.Task
import logic.usecase.task.GetTaskUseCase
import ui.io.Display
import ui.io.InputReader

class SelectTaskUi(
    private val display: Display,
    private val inputReader: InputReader,
    private val getTaskUseCase: GetTaskUseCase
) {
    operator fun invoke(tasks: List<Task>): Task {

        val selection = inputReader.readInt(
            prompt = "Enter task number to take action on",
            min = 1,
            max = tasks.size
        )

        val selectedTask = getTaskUseCase(tasks[selection - 1].id)
        display.write(prompt = "You selected: ${selectedTask.title} (ID: ${selectedTask.id})")
        return selectedTask
    }
}