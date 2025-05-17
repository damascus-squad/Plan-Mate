package org.damascus.data.mapper

import org.damascus.data.dto.TaskStateDTO
import org.damascus.logic.model.TaskState

fun TaskState.toDto() = TaskStateDTO(
    id = id,
    name = name,
    projectReferencesCount = projectReferencesCount
)

fun TaskStateDTO.toModel() = TaskState(
    id = id,
    name = name,
    projectReferencesCount = projectReferencesCount
)