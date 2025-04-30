package org.damascus.logic.entities

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class ActionLog(
    val userName: String,
    val taskId: UUID,
    val projectId: UUID,
    val actionDate: LocalDateTime,
    val currentState: String,
    val targetedState: String
){

}