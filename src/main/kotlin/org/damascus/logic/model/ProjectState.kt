package org.damascus.logic.model

data class ProjectState(
    val taskStateFrequency: Map<TaskState, Int>
)