package org.damascus.data.csv

import data.model.*

object FileDataSerializer {

    fun serializeUser(user: User): String {
        return listOf(
            user.id.toString(),
            user.username,
            user.password,
            user.role
        ).joinToString(",")
    }

    fun serializeProject(project: Project): String {
        val matesString = project.assignedMates.joinToString(";") { it.id.toString() }
        return listOf(
            project.id.toString(),
            project.name,
            matesString,
            project.creationDate.toString()
        ).joinToString(",")
    }

    fun serializeTask(task: Task): String {
        val assigneeId = task.assignee?.id?.toString()
        return listOf(
            task.id.toString(),
            task.projectId.toString(),
            task.title,
            task.description,
            assigneeId ?: "",
            task.state.id.toString(),
            task.creationDate.toString()
        ).joinToString(",")

    }

    fun serializeState(state: State): String {
        return listOf(state.id.toString(), state.name).joinToString(",")
    }

    fun serializeHistory(history: History): String {
        return listOf(
            history.id.toString(),
            history.projectID.toString(),
            history.entityId.toString(),
            history.entityType,
            history.changedBy.toString(),
            history.oldState ?: "",
            history.newState ?: "",
            history.timestamp.toString()
        ).joinToString(",")
    }
}
