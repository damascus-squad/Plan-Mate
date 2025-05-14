package org.damascus.data.mapper

import org.damascus.data.dto.ProjectDTO
import org.damascus.logic.model.Project

fun ProjectDTO.toModel() = Project(
    id = id,
    name = name,
    assignedMatesIds = assignedMatesIds,
    allowedStatesIds = allowedStatesIds,
    creationDate = creationDate
)

fun Project.toDto() = ProjectDTO(
    id = id,
    name = name,
    assignedMatesIds = assignedMatesIds,
    allowedStatesIds = allowedStatesIds,
    creationDate = creationDate
)