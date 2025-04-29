package org.damascus.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class ActionLog(
    val userName: String,
    val taskId: UUID = UUID.randomUUID(),
    val projectId: UUID = UUID.randomUUID(),
    val actionDate: LocalDateTime,
    val currentState: String,
    val targetedState: String
){

}