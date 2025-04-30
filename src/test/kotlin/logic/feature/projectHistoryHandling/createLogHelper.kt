package logic.feature.projectHistoryHandling

import kotlinx.datetime.LocalDateTime
import org.damascus.logic.entities.ActionLog
import java.util.UUID

fun createFakeActionLog(
    userName: String = "TestMate",
    taskId: UUID,
    currentState: String = "TODO",
    targetedState: String = "In-progress",
    projectId: UUID,
    actionDate: LocalDateTime
) = ActionLog(
    userName = userName,
    taskId = taskId,
    currentState = currentState,
    targetedState = targetedState,
    projectId = projectId,
    actionDate = actionDate
)
