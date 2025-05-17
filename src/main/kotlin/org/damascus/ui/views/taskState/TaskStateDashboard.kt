package org.damascus.ui.views.taskState

import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.koin.core.annotation.Single

@Single
class TaskStateDashboard(
    private val display: Display,
    private val createStateUi: CreateTaskStateUi,
    private val deleteStateUi: DeleteTaskStateUi,
    private val updateStateUi: UpdateTaskStateUi,
    private val getAllTaskStateUi: GetAllTaskStateUi
) {

    operator suspend fun invoke() {
        display.displayMenu(
            listOf(
                UiAction("Show All States", { getAllTaskStateUi() }),
                UiAction("Create New State", { createStateUi() }),
                UiAction("Delete State", { deleteStateUi() }),
                UiAction("Update State", { updateStateUi() }),
            ),
            menuTitle = "\n⚙️ Task State Management"
        )
    }

}

