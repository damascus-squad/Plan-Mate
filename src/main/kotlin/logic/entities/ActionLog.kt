package org.damascus.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID
import org.damascus.logic.model.State

data class ActionLog(
    val userId: UUID,
    val taskId: UUID,
    val projectId: UUID,
    val actionDate: LocalDateTime,
    val currentState: State,
    val newState: State,
    val actionType: ActionType) {
    companion object {
        val NO_UUID: UUID = UUID(0, 0)
        val NO_STATE = State(
            id = NO_UUID,
            name = "Nothing"
        )
    }
}