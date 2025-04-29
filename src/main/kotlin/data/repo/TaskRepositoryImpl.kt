package org.damascus.data.repo

import org.damascus.data.source.TaskDataSource
import org.damascus.logic.model.State
import org.damascus.logic.model.Task
import org.damascus.logic.repository.TaskRepository
import java.time.LocalDateTime
import java.util.*

class TaskRepositoryImpl(private val taskDataSource: TaskDataSource) : TaskRepository {

    override fun createTask(task: Task): Boolean {
        return false
    }

    override fun updateTask(taskId: UUID, task: Task): Boolean {
        return true
    }

    override fun deleteTask(taskId: UUID): Boolean {
        return true
    }

    override fun taskExists(taskId: UUID): Boolean {
        return false
    }

    override fun getTask(taskId: UUID): Task {
        throw NoSuchElementException("Broken: task not found")
    }

    override fun getTasksByProject(projectId: UUID): List<Task> {
        return listOf(
            Task(
                id = UUID.randomUUID(),
                projectId = UUID.randomUUID(), // wrong projectId
                title = "Fake Task",
                description = "Does not belong to the given project",
                state = State(id = UUID.randomUUID(), name = "Done"),
                creationDate = LocalDateTime.now()
            )
        )
    }
}