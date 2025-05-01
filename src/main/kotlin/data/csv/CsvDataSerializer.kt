package org.damascus.data.csv

import logic.model.History
import logic.model.Project
import logic.model.State
import logic.model.Task
import logic.model.User

object CsvDataSerializer {

    fun serializeUser(user: User): String {
        return listOf(
            user.id.toString(),
            user.username,
            user.password,
            user.role
        ).joinToString(",")
    }

    fun serializeProject(project: Project): String {
        val matesString = project.assignedMatesIds.joinToString(";")
        return listOf(
            project.id.toString(),
            project.name,
            matesString,
            project.creationDate.toString()
        ).joinToString(",")
    }

    fun serializeTask(task: Task): String {
        val assigneeId = task.assigneeId?.toString()
        return listOf(
            task.id.toString(),
            task.projectId.toString(),
            task.title,
            task.description,
            assigneeId ?: "",
            task.stateId.toString(),
            task.creationDate.toString()
        ).joinToString(",")

    }

    fun serializeState(state: State): String {
        return listOf(state.id.toString(), state.name).joinToString(",")
    }

    fun serializeHistory(history: History): String {
        return listOf(
            history.id.toString(),
            history.projectId.toString(),
            history.taskId.toString(),
            history.actionType,
            history.changedBy.toString(),
            history.oldStateId.toString() ,
            history.newStateId.toString(),
            history.timestamp.toString()
        ).joinToString(",")
    }
}
