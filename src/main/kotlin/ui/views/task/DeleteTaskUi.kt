package org.damascus.ui.views.task

import logic.model.Task
import logic.model.User
import logic.usecase.task.DeleteTaskUseCase
import ui.io.InputReader

class DeleteTaskUi(
    private val inputReader: InputReader,
    private val deleteTaskUseCase: DeleteTaskUseCase
) {
    operator fun invoke(task: Task, user: User) {
        val confirm = inputReader.readBoolean("Are you sure you want to delete this task? (yes/no): ")
        if (confirm) {
            deleteTaskUseCase(task.id)
            println("🗑️ Task deleted successfully!")
        } else {
            println("❌ Deletion canceled.")
        }
    }
}