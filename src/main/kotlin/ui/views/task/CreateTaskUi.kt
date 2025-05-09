package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.TaskAlreadyExistsException
import logic.model.*
import logic.usecase.auditLog.SaveLogUseCase
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
    private val saveLogUseCase: SaveLogUseCase
) {
    operator fun invoke (currentProject: Project, currentUser: User) {
        val title = inputReader.readString("Enter task title: ")
        val description = inputReader.readString("Enter task description: ")
        val availableMates = getAllMatesUseCase().filter { it.id in currentProject.assignedMatesIds }
        val assigneeId = selectMateUi(availableMates)

//        println("Available task states:")
//        dummyStates.forEachIndexed { index, state ->
//            println("${index + 1}. ${state.name}")
//        }
//
//        val selectedStateIndex = inputReader.readInt("Select task state (1-${dummyStates.size}): ", 1, dummyStates.size)
//        val stateId = dummyStates[selectedStateIndex - 1].id
//
        val newTask = Task(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            projectId = currentProject.id,
            assigneeId = assigneeId.id,
            stateId = UUID.randomUUID(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        try {
            createTaskUseCase(newTask)
            println("✅ Task '${newTask.title}' created successfully!")
//            viewProjectSwimlaneView(dummyStates, projectId)
            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = newTask.id,
                    actionType = ActionType.TASK_CREATED,
                    userId = currentUser.id,
                    currentStateId = UUID.randomUUID(),
                    newStateId = newTask.stateId,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } catch (e: TaskAlreadyExistsException) {
            println("❌ Failed to create task: ${e.message}")
        }
    }
}