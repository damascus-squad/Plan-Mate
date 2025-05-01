package org.damascus.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID
import org.damascus.logic.model.State

data class ActionLog(
    val userId: UUID,
    val taskId: UUID = NO_UUID,
    val projectId: UUID = NO_UUID,
    val actionDate: LocalDateTime,
    val currentState: State = NO_STATE,
    val targetedState: State = NO_STATE,
    val actionType: ActionType
){
    companion object {
        val NO_UUID: UUID = UUID(0, 0)
        val NO_STATE = State(
            id = NO_UUID,
            name = "Nothing"
        )
    }
}