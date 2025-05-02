package org.damascus.logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID
import logic.model.State

data class History(
    val id: UUID,
    val projectId: UUID,
    val taskId: UUID,
    val actionType: ActionType,
    val userId: UUID,
    val currentState: UUID,
    val newState: UUID,
    val actionDate: LocalDateTime,
)
{
    companion object {
        val NO_UUID: UUID = UUID(0, 0)
        val NO_STATE = State(
            id = NO_UUID,
            name = "Nothing"
        )
    }
}




//package logic.model
//
//import kotlinx.datetime.LocalDateTime
//import java.util.UUID
//
//data class History(
//    val id: UUID,
//    val projectId: UUID,
//    val taskId: UUID,
//    val actionType: String,
//    val userId: UUID,
//    val currentState: UUID,
//    val newStateId: UUID,
//    val timestamp: LocalDateTime
//)