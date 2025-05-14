package org.damascus.ui.views.projectDashboard

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.*
import logic.model.*
import logic.repo.TaskStateRepository
import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.GetProjectUseCase
import logic.usecase.project.AssignMateUseCase
import logic.usecase.project.DeleteProjectUseCase
import logic.usecase.project.UpdateProjectUseCase
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.CreateTaskUseCase
import logic.usecase.task.DeleteTaskUseCase
import logic.usecase.task.GetTasksByProjectUseCase
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import org.damascus.logic.usecase.project.UnassignMateUseCase
import org.damascus.ui.views.taskState.TaskStateCLI
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
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val assignMateUseCase: AssignMateUseCase,
    private val removeMate: UnassignMateUseCase,
    private val getAllMatesUseCase: GetAllMatesUseCase,
    private val getLogsByProjectIdUseCase: GetLogsByProjectIdUseCase,
    private val taskStateCLI: TaskStateCLI,
    private val taskStateRepository: TaskStateRepository
    ) : ProjectDashboardController {

    override fun start(projectId: UUID, currentUser: User) {

        val adminActions = listOf(
            UiAction("Edit Project") { editProject(projectId, currentUser) },
            UiAction("Delete Project") { deleteProject(projectId) },
            UiAction("Assign Mate") { assignMateToProject(projectId, selectMateFromList()) },
            UiAction("Remove Mate") { unassignMateFromProject(projectId, selectMateFromList()) },
            UiAction("Show History") { showHistory(projectId, currentUser) },
            UiAction("Create Task") { createTask(projectId, currentUser) },
            UiAction("Display Tasks Board") { viewProjectSwimlaneView(getCurrentStates(), projectId) },
            UiAction("Manage Task State"){ taskStateCLI.start() }
        )

        val mateActions = listOf(
            UiAction("Show History") { },
            UiAction("Create Task") { createTask(projectId, currentUser) },
            UiAction("Manage Task State"){ taskStateCLI.start() }
        )

        val actions = when (currentUser.userRole) {
            UserRole.ADMIN -> adminActions
            UserRole.MATE -> mateActions
        }

        display.displayMenu(actions, menuTitle = "Project Dashboard")
        viewProjectSwimlaneView(getCurrentStates(), projectId)
    }

    private fun getCurrentStates(): List<TaskState> {
        return taskStateRepository.getAllStates()
    }

    override fun deleteProject(projectId: UUID) {
        try {
            val confirm = inputReader.readString("Are you sure you want to delete project $projectId? (yes/no): ")
            if (confirm.equals("yes", ignoreCase = true)) {
                deleteProjectUseCase(projectId)
                println("🗑️ Project $projectId deleted successfully!")
            } else {
                println("❌ Project deletion canceled.")
            }
        } catch (e: ProjectNotFoundException) {
            println("Error: ${e.message}")
        }
    }

    private fun showHistory(projectId: UUID, user: User) {
        val projectLogs = getLogsByProjectIdUseCase(projectId)

        if (projectLogs.isEmpty()) {
            println("ℹ️ No history found for this project.")
            return
        }

        val tasks = getTasksByProjectUseCase(projectId)
        val taskMap = tasks.associateBy { it.id }

        val project = getProjectUseCase(projectId)

        projectLogs.forEach { log ->
            val actionDate = log.actionDate.toString()
            val actionDescription = when (log.actionType) {
                ActionType.TASK_CREATED -> {
                    val taskTitle = taskMap[log.taskId]?.title ?: "Unknown Task"
                    """created the task "$taskTitle""""
                }

                ActionType.TASK_DELETED -> {
                    val taskTitle = taskMap[log.taskId]?.title ?: "Unknown Task"
                    """deleted the task "$taskTitle""""
                }

                ActionType.TASK_STATE_CHANGED -> {
                    val taskTitle = taskMap[log.taskId]?.title ?: "Unknown Task"
                    val fromState = getTaskStateByIdUseCase(log.currentStateId).name
                    val toState = getTaskStateByIdUseCase(log.newStateId).name
                    """moved the task "$taskTitle" from "$fromState" to "$toState""""
                }

                ActionType.PROJECT_CREATED -> """created the project "${project.name}""""

                ActionType.PROJECT_MODIFIED -> {
                    """modified the project "${project.name}""""
                }

                ActionType.PROJECT_DELETED -> """deleted the project "${project.name}""""
            }

            println("🕒 [$actionDate] ${user.username} $actionDescription")
        }
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

    override fun editProject(projectId: UUID, currentUser: User) {
        val updatedProject = getProjectUseCase(projectId)

        display.displayMenu(
            listOf(
                UiAction("Title") { updateField("Title", updatedProject, currentUser = currentUser) },
                UiAction("Assign Mate") { updateField("Assign Mate", updatedProject, currentUser = currentUser) },
                UiAction("Remove Mate") { updateField("Remove Mate", updatedProject, currentUser = currentUser) },
            ),
            menuTitle = "\nSelect the field you want to update:"
        )

        updateProjectUseCase(projectId, updatedProject)

        println("✅ Task updated successfully!")
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

    override fun createTask(projectId: UUID, currentUser: User) {
        val title = inputReader.readString("Enter task title: ")
        val description = inputReader.readString("Enter task description: ")
        val project = getProjectUseCase(projectId)
        val assigneeInput: String
        var assigneeId: UUID? = null
        val mates = getAllMatesUseCase().filter { it.id in project.assignedMatesIds }

        if (mates.isEmpty()) {
            println("⚠️ No mates assigned to this project.")
        } else {
            println("👥 Available Mates:")
            mates.forEachIndexed { index, mate ->
                println("${index + 1}. ${mate.username} (ID: ${mate.id})")
            }

            assigneeInput = inputReader.readString("Enter assignee ID (leave blank if none): ")
            assigneeId = if (assigneeInput.isNotBlank()) {
                val selectedMateIndex = assigneeInput.toIntOrNull()
                if (selectedMateIndex != null && selectedMateIndex in 1..mates.size) {
                    mates[selectedMateIndex - 1].id
                } else {
                    println("⚠️ Invalid assignee number. Skipping assignee.")
                    null
                }
            } else null
        }


        println("Available task states:")
        getCurrentStates().forEachIndexed { index, state ->
            println("${index + 1}. ${state.name}")
        }

        val selectedStateIndex = inputReader.readInt("Select task state (1-${getCurrentStates().size}): ", 1, getCurrentStates().size)
        val stateId = getCurrentStates()[selectedStateIndex - 1].id

        val newTask = Task(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            projectId = projectId,
            assigneeId = assigneeId,
            stateId = stateId,
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        try {
            createTaskUseCase(newTask)
            println("✅ Task '${newTask.title}' created successfully!")
            viewProjectSwimlaneView(getCurrentStates(), projectId)
            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = projectId,
                    taskId = newTask.id,
                    actionType = ActionType.TASK_CREATED,
                    userId = currentUser.id,
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
        if (assignMateUseCase(projectId, mateId)) {
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

    override fun unassignMateFromProject(projectId: UUID, mateId: UUID) {
        if (removeMate(projectId, mateId)) {
            println("👤 Mate unassigned from project successfully!")
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
            println("⚠️ Failed to unassign mate.")
        }
    }

    private fun updateField(fieldName: String, updatedProject: Project, currentUser: User): Project {
        println("You selected: $fieldName")
        when (fieldName) {
            "Title" -> {
                val newTitle = inputReader.readString("Enter new title (or type 's' to keep current): ")

                if (newTitle.lowercase() != "s") {
                    saveLogUseCase(
                        History(
                            id = UUID.randomUUID(),
                            projectId = updatedProject.id,
                            taskId = History.NO_UUID,
                            actionType = ActionType.PROJECT_MODIFIED,
                            userId = currentUser.id,
                            currentStateId = History.NO_UUID,
                            newStateId = History.NO_UUID,
                            actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                    )
                    println("✅ Title updated successfully!")
                    return updatedProject.copy(name = newTitle)
                } else {
                    println("ℹ️ Title unchanged.")
                    return updatedProject
                }
            }

            "Assign Mate" -> {
                assignMateToProject(projectId = updatedProject.id, selectMateFromList())
            }

            "Remove Mate" -> {
                val removedMate = selectMateFromList()
                saveLogUseCase(
                    History(
                        id = UUID.randomUUID(),
                        projectId = updatedProject.id,
                        taskId = History.NO_UUID,
                        actionType = ActionType.PROJECT_MODIFIED,
                        userId = currentUser.id,
                        currentStateId = History.NO_UUID,
                        newStateId = History.NO_UUID,
                        actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                )
                updatedProject.assignedMatesIds.remove(removedMate)
                return updatedProject
            }
        }
        return updatedProject
    }

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