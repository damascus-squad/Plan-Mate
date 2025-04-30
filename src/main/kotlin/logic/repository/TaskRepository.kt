package org.damascus.logic.repository

import model.Task
import java.util.UUID

interface TaskRepository {
    fun create(task: Task) : Boolean
    fun update(taskId: UUID, task: Task): Boolean
    fun delete(taskId: UUID): Boolean
    fun exists(taskId: UUID): Boolean
    fun get(taskId: UUID): Task
    fun getByProject(projectId: UUID): List<Task>
}