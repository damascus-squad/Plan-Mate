package org.damascus.logic.model

import kotlinx.datetime.LocalDateTime
import java.util.*

data class History(
    val id: UUID,
    val projectId: UUID,
    val taskId: UUID,
    val actionType: ActionType,
    val userId: UUID,
    val currentState: String?,
    val newState: String?,
    val actionDate: LocalDateTime,
) {
    companion object {
        val NO_UUID: UUID = UUID(0, 0)
        val NO_TASK_STATE = TaskState(
            id = NO_UUID,
            name = "Nothing",
            projectReferencesCount = 0,
        )
    }
}