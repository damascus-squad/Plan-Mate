package logic.useCase

import kotlinx.datetime.LocalDateTime
import logic.model.State
import org.damascus.logic.model.History
import org.damascus.logic.model.ActionType
import java.util.UUID

fun createFakeActionLog(
    id: UUID = UUID.randomUUID(),
    userId: UUID = UUID.randomUUID(),
    taskId: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    actionDate: LocalDateTime,
    currentState: State,
    targetedState: State,
    actionType: ActionType = ActionType.TASK_STATE_CHANGED,
): History = History(
    id = id,
    taskId = taskId,
    projectId = projectId,
    currentState = currentState,
    newState = targetedState,
    actionDate = actionDate,
    actionType = actionType,
    userId = userId,
)
