package org.damascus.ui.views.projectDashboard

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.NoMatesAvailableException
import logic.exception.TaskAlreadyExistsException
import logic.exception.TaskNotFoundException
import logic.model.*
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.GetProjectUseCase
import logic.usecase.project.AssignMateUseCase
import logic.usecase.project.UpdateProjectUseCase
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.CreateTaskUseCase
import logic.usecase.task.DeleteTaskUseCase
import logic.usecase.task.GetTasksByProjectUseCase
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import org.damascus.logic.usecase.project.UnassignMateUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.UiAction
import ui.util.printTable
import java.util.*
import kotlin.system.exitProcess


class ProjectDashboardCli(
    private val display: Display,
    private val inputReader: InputReader,
    private val getProjectUseCase: GetProjectUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val assignMateUseCase: AssignMateUseCase,
    private val removeMate: UnassignMateUseCase,
    private val getAllMatesUseCase: GetAllMatesUseCase,
) : ProjectDashboardController {

    override fun start(projectId: UUID, currentUser: User) {
        val dummyStates = listOf(
            TaskState(UUID.randomUUID(), "TODO"),
            TaskState(UUID.randomUUID(), "In Progress"),
            TaskState(UUID.randomUUID(), "Done"),
        )

        viewProjectSwimlaneView(dummyStates, projectId)

        val adminActions = listOf(
            UiAction("Edit Project") { editProject(projectId) },
            UiAction("Delete Project") { },
            UiAction("Assign Mate") { assignMateToProject(projectId, selectMateFromList()) },
            UiAction("Remove Mate") { removeMate(projectId, selectMateFromList()) },
            UiAction("Show History") { },
            UiAction("Create Task") { createTask(projectId) }
        )

        val mateActions = listOf(
            UiAction("Show History") { },
            UiAction("Create Task") { createTask(projectId) }
        )

        val actions = when (currentUser.userRole) {
            UserRole.ADMIN -> adminActions
            UserRole.MATE -> mateActions
        }

        display.displayMenu(actions, menuTitle = "Project Dashboard")
    }

    private fun viewProjectSwimlaneView(allowedStates: List<TaskState>, projectId: UUID) {
        val projectTasks = getTasksByProjectUseCase(projectId)

        val groupedTasks = allowedStates.map { state ->
            state.name to projectTasks.filter { it.stateId == state.id }
        }

        val maxRows = groupedTasks.maxOf { it.second.size }

        val tableRows = (0 until maxRows).map { rowIndex ->
            groupedTasks.map { (_, tasks) ->
                tasks.getOrNull(rowIndex)?.title ?: ""
            }
        }

        val headers = allowedStates.map { it.name }
        printTable(headers, tableRows)
    }

    override fun editProject(projectId: UUID) {
//        val updatedProject = getProjectUseCase(projectId)
//
//        display.displayMenu(
//            listOf(
//                UiAction("Title") { updateField("Title", updatedProject) },
//                UiAction("Assign Mate") { updateField("Assign Mate", updatedProject) }
//            ),
//            menuTitle = "\nSelect the field you want to update:"
//        )
//
//        updateProjectUseCase(projectId, updatedProject)
//
//        println("✅ Task updated successfully!")
    }

    override fun deleteTask(taskId: UUID) {
        try {
            val confirm = inputReader.readString("Are you sure you want to delete task $taskId? (yes/no): ")
            if (confirm.equals("yes", ignoreCase = true)) {
                deleteTaskUseCase(taskId)
                println("🗑️ Task $taskId deleted successfully!")
            } else {
                println("❌ Task deletion canceled.")
            }
        } catch (e: TaskNotFoundException) {
            println("Error: ${e.message}")
        }
    }

    override fun createTask(projectId: UUID) {
        val title = inputReader.readString("Enter task title: ")
        val description = inputReader.readString("Enter task description: ")

        val assigneeInput = inputReader.readString("Enter assignee ID (leave blank if none): ")

        val stateInput = inputReader.readString("Enter task state ID: ")

        val assigneeId = if (assigneeInput.isNotBlank()) UUID.fromString(assigneeInput) else null
        val stateId = UUID.fromString(stateInput)

        val newTask = Task(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            projectId = projectId,
            assigneeId = UUID.randomUUID(),
            stateId = UUID.randomUUID(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        try {
            createTaskUseCase(newTask)
            println("✅ Task '${newTask.title}' created successfully!")
            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = projectId,
                    taskId = newTask.id,
                    actionType = ActionType.TASK_CREATED,
                    //TODO : add user id
                    userId = UUID.randomUUID(),
                    currentStateId = stateId,
                    newStateId = newTask.stateId,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } catch (e: TaskAlreadyExistsException) {
            println("❌ Failed to create task: ${e.message}")
        }
    }

    override fun assignMateToProject(projectId: UUID, mateId: UUID) {
        if (assignMateUseCase(projectId,mateId)) {
            println("👥 Mate assigned to project successfully!")
            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = projectId,
                    taskId = UUID.randomUUID(),
                    actionType = ActionType.PROJECT_MODIFIED,
                    userId = mateId,
                    currentStateId = History.NO_UUID,
                    newStateId = History.NO_UUID,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } else {
            println("⚠️ Failed to assign mate.")
        }
    }

//    private fun updateField(fieldName: String, updatedProject: Project): Project {
//        println("You selected: $fieldName")
//        when (fieldName) {
//            "Title" -> {
//                val newTitle = inputReader.readString("Enter new title (leave blank to keep existing): ")
//                if (newTitle.isNotBlank()) {
//                    saveLogUseCase(
//                        History(
//                            id = UUID.randomUUID(),
//                            projectId = updatedProject.id,
//                            taskId = History.NO_UUID,
//                            actionType = ActionType.PROJECT_MODIFIED,
//                            //TODO : add user id
//                            userId = UUID.randomUUID(),
//                            currentStateId = History.NO_UUID,
//                            newStateId = History.NO_UUID,
//                            actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
//                        )
//                    )
//                    return updatedProject.copy(name = newTitle)
//                } else {
//                    println("No change to title.")
//                    return updatedProject
//                }
//            }
//
//            "Assign Mate" -> {
//                //TODO: mate name
//                val mateIdStr = inputReader.readString("Enter mate ID to assign/remove: ")
//                try {
//                    val mateId = UUID.fromString(mateIdStr)
//                    val action = inputReader.readString("Type 'assign' to assign or 'remove' to unassign: ").lowercase()
//
//                    val shouldAssign = when (action) {
//                        "assign" -> true
//                        "remove" -> false
//                        else -> {
//                            println("Invalid action. Use 'assign' or 'remove'.")
//                            return updatedProject
//                        }
//                    }
//                    assignMateToProject(updatedProject.id, mateId, shouldAssign)
//                    println("Mate $action successfully.")
//                    return updatedProject
//                } catch (e: IllegalArgumentException) {
//                    println("Error: ${e.message}.")
//                    return updatedProject
//                }
//            }
//
//            else -> return updatedProject
//        }
//    }

    private fun selectMateFromList(): UUID {
        val availableMates = try {
            getAllMatesUseCase()
        } catch (e: NoMatesAvailableException) {
            println("❌ ${e.message}")
            exitProcess(1)
        }

        println("\n👥 Available Mates:")
        val headers = listOf("ID", "Name")
        val rows = availableMates.mapIndexed { index, mate ->
            listOf((index + 1).toString(), mate.username)
        }
        printTable(headers, rows)

        val selectedIndex = inputReader.readInt(
            prompt = "Enter the number of the mate to assign: ",
            min = 1,
            max = availableMates.size
        )

        return availableMates[selectedIndex - 1].id
    }
}