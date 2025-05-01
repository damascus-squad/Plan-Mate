package logic.feature.projectHistoryHandling

import kotlinx.datetime.LocalDateTime

import org.damascus.logic.model.State
import org.damascus.logic.entities.ActionLog
import org.damascus.logic.entities.ActionType
import java.util.UUID

fun createFakeActionLog(
    userId: UUID = UUID.randomUUID(),
    taskId: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    actionDate: LocalDateTime,
    currentState: State,
    targetedState: State,
    actionType: ActionType = ActionType.TASK_STATE_CHANGED,
): ActionLog = ActionLog(
    taskId = taskId,
    projectId = projectId,
    currentState = currentState,
    targetedState = targetedState,
    actionDate = actionDate,
    actionType = actionType,
    userId = userId,
)
