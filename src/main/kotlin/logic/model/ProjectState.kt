package org.damascus.logic.model

import logic.model.TaskState

data class ProjectState(
    val taskStateFrequency: Map<TaskState, Int>
)