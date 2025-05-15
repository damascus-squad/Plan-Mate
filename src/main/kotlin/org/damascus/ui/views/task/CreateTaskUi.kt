package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.exception.TaskAlreadyExistsException
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.Task
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.user.SelectMateUi
import java.util.*

class CreateTaskUi(
    private val inputReader: InputReader,
    private val selectMateUi: SelectMateUi,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateUseCase: ManageMateUseCase,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {
    operator suspend fun invoke(currentProject: Project, currentUser: User) {
        val title = inputReader.readString(prompt = "Enter task title")
        val description = inputReader.readString(prompt = "Enter task description")
        val availableMates = manageMateUseCase.getAllMates().filter { it.id in currentProject.assignedMatesIds }
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
            manageTaskUseCase.createTask(newTask)
            println("✅ Task '${newTask.title}' created successfully!")
            manageAuditLogUseCase.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = newTask.id,
                    actionType = ActionType.TASK_CREATED,
                    userId = currentUser.id,
                    currentState = null,
                    newState = manageTaskStateUseCase.getTaskState(stateId).name,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } catch (e: TaskAlreadyExistsException) {
            println("❌ Failed to create task: ${e.message}")
        }
    }
}