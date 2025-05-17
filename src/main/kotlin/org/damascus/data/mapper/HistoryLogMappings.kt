package org.damascus.data.mapper

import org.damascus.data.dto.HistoryLogDTO
import org.damascus.logic.model.History

fun History.toDto() = HistoryLogDTO(
    id = id,
    projectId = projectId,
    taskId = taskId,
    actionType = actionType,
    userId = userId,
    currentState = currentState,
    newState = newState,
    actionDate = actionDate
)

fun HistoryLogDTO.toModel() = History(
    id = id,
    projectId = projectId,
    taskId = taskId,
    actionType = actionType,
    userId = userId,
    currentState = currentState,
    newState = newState,
    actionDate = actionDate
)