package org.damascus.data.mapper

import org.damascus.data.dto.TaskDTO
import org.damascus.logic.model.Task

fun Task.toDto() = TaskDTO(
    id = id,
    projectId = projectId,
    title = title,
    description = description,
    assigneeId = assigneeId,
    stateId = stateId,
    creationDate = creationDate
)

fun TaskDTO.toModel() = Task(
    id = id,
    projectId = projectId,
    title = title,
    description = description,
    assigneeId = assigneeId,
    stateId = stateId,
    creationDate = creationDate
)