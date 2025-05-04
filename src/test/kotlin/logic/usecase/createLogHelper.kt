package logic.usecase

import kotlinx.datetime.LocalDateTime
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import java.util.*

fun createFakeActionLog(
    id: UUID = UUID.randomUUID(),
    userId: UUID = UUID.randomUUID(),
    taskId: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    actionDate: LocalDateTime,
    currentStateId: UUID,
    targetedStateId: UUID,
    actionType: ActionType = ActionType.TASK_STATE_CHANGED,
): History = History(
    id = id,
    taskId = taskId,
    projectId = projectId,
    currentStateId = currentStateId,
    newStateId = targetedStateId,
    actionDate = actionDate,
    actionType = actionType,
    userId = userId,
)
