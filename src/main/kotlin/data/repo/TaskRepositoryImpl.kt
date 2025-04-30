package org.damascus.data.repo

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.*
import org.damascus.data.source.TaskDataSource
import org.damascus.logic.repository.TaskRepository
import java.time.LocalDateTime
import java.util.*

class TaskRepositoryImpl(private val taskDataSource: TaskDataSource<Task>) : TaskRepository {

    override fun create(task: Task): Boolean {
        return false
    }

    override fun update(taskId: UUID, task: Task): Boolean {
        return true
    }

    override fun delete(taskId: UUID): Boolean {
        return true
    }

    override fun exists(taskId: UUID): Boolean {
        return false
    }

    override fun get(taskId: UUID): Task {
        throw NoSuchElementException("Broken: task not found")
    }

    override fun getByProject(projectId: UUID): List<Task> {
        return listOf(
            Task(
                id = UUID.randomUUID(),
                projectId = UUID.randomUUID(), // wrong projectId
                title = "Fake Task",
                description = "Does not belong to the given project",
                state = State(id = UUID.randomUUID(), name = "Done"),
                creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            )
        )
    }
}