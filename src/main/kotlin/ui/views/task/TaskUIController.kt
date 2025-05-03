package org.damascus.ui.views.task

import java.util.*

interface TaskUIController {
    fun editTask(taskId: UUID)
    fun deleteTask(taskId: UUID)
    fun viewTaskDetails(taskId: UUID)
}