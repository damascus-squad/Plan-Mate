package org.damascus.ui.views.taskState

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.ui.io.InputReader
import org.koin.core.annotation.Single
import java.util.*

@Single
class CreateTaskStateUi(
    private val inputReader: InputReader,
    private val manageTaskState: ManageTaskStateUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase
) {
    operator fun invoke() {
        val taskStateName = inputReader.readString("Enter name for new state: ")
        manageTaskState.createTaskState(taskStateName)
        println("✅ Task state created successfully.")

        manageAuditLogUseCase.saveLog(
            History(
                id = UUID.randomUUID(),
                projectId = History.NO_UUID,
                taskId = History.NO_UUID,
                actionType = ActionType.TASK_STATE_CHANGED,
                userId = UUID.randomUUID(),
                currentState = null,
                newState = taskStateName,
                actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )
    }
}