package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.TaskAlreadyExistsException
import logic.model.*
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.CreateTaskUseCase
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import org.damascus.ui.views.user.SelectMateUi
import ui.io.InputReader
import java.util.*

class CreateTaskUi(
    private val inputReader: InputReader,
    private val selectMateUi: SelectMateUi,
    private val createTaskUseCase: CreateTaskUseCase,
    private val getAllMatesUseCase: GetAllMatesUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase
) {
    operator fun invoke (currentProject: Project, currentUser: User) {
        val title = inputReader.readString(prompt = "Enter task title")
        val description = inputReader.readString(prompt = "Enter task description")
        val availableMates = getAllMatesUseCase().filter { it.id in currentProject.assignedMatesIds }
        val stateId = currentProject.allowedStatesIds.first()
        val assignee: User? = if (currentUser.userRole == UserRole.ADMIN) {
            selectMateUi(availableMates)
        } else null

        val newTask = Task(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            projectId = currentProject.id,
            assigneeId = assignee?.id,
            stateId = stateId,
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        try {
            createTaskUseCase(newTask)
            println("✅ Task '${newTask.title}' created successfully!")
            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = newTask.id,
                    actionType = ActionType.TASK_CREATED,
                    userId = currentUser.id,
                    currentState = null,
                    newState = getTaskStateByIdUseCase(stateId).name,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } catch (e: TaskAlreadyExistsException) {
            println("❌ Failed to create task: ${e.message}")
        }
    }
}