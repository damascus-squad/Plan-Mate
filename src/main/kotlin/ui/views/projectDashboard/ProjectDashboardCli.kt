package ui.views.projectDashboard

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.exception.*
import org.damascus.logic.model.*
import org.damascus.logic.repo.TaskStateRepository
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.logic.usecase.project.GetProjectStateUseCase
import org.damascus.logic.usecase.project.ManageMateAssignmentUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.util.UiAction
import org.damascus.ui.util.printTable
import ui.views.taskState.TaskStateCli
import java.util.*
import kotlin.system.exitProcess

class ProjectDashboardCli(
    private val display: Display,
    private val inputReader: InputReader,
    private val manageTask: ManageTaskUseCase,
    private val manageProject: ManageProjectUseCase,
    private val manageAuditLog: ManageAuditLogUseCase,
    private val manageMateAssignment: ManageMateAssignmentUseCase,
    private val manageMate: ManageMateUseCase,
    private val taskStateCLI: TaskStateCli,
    private val taskStateRepo: TaskStateRepository
) {

    fun start(projectId: UUID, currentUser: User) {
        val adminActions = listOf(
            UiAction("Edit Project", { editProject(projectId, currentUser) }),
            UiAction("Delete Project", { deleteProject(projectId) }),
            UiAction("Assign Mate", { assignMateToProject(projectId, selectMateFromList()) }),
            UiAction("Remove Mate", { unassignMateFromProject(projectId, selectMateFromList()) }),
            UiAction("Show History", { showHistory(projectId, currentUser) }),
            UiAction("Create Task", { createTask(projectId, currentUser) }),
            UiAction("Display Tasks Board", { viewProjectSwimlaneView(getCurrentStates(), projectId) }),
            UiAction("Manage Task State", { taskStateCLI.start() })
        )

        val mateActions = listOf(
            UiAction("Show History", { showHistory(projectId, currentUser) }),
            UiAction("Create Task", { createTask(projectId, currentUser) }),
            UiAction("Manage Task State", { taskStateCLI.start() })
        )

        val actions = when (currentUser.userRole) {
            UserRole.ADMIN -> adminActions
            UserRole.MATE -> mateActions
        }

        display.displayMenu(actions, menuTitle = "Project Dashboard")
        viewProjectSwimlaneView(getCurrentStates(), projectId)
    }

    private fun getCurrentStates(): List<TaskState> {
        return taskStateRepo.getAllStates()
    }

    private fun deleteProject(projectId: UUID) {
        try {
            val confirm = inputReader.readString("Are you sure you want to delete project $projectId? (yes/no): ")
            if (confirm.equals("yes", ignoreCase = true)) {
                manageProject.deleteProject(projectId)
                println("🗑️ Project $projectId deleted successfully!")
            } else {
                println("❌ Project deletion canceled.")
            }
        } catch (e: ProjectNotFoundException) {
            println("Error: ${e.message}")
        }
    }

    private fun showHistory(projectId: UUID, user: User) {
        val projectLogs = try {
            manageAuditLog.getProjectLogs(projectId)
        } catch (e: NoLogException) {
            println("ℹ️ No history found for this project.")
            return
        }

        val tasks = manageTask.getProjectTasks(projectId)
        val taskMap = tasks.associateBy { it.id }

        val project = manageProject.getProject(projectId)

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
                    // Redundant, the same as TASK_TITLE_MODIFIED
                    val taskTitle = taskMap[log.taskId]?.title ?: "Unknown Task"
                    val fromState = log.currentState ?: "Unknown"
                    val toState = log.newState ?: "Unknown"
                    """moved the task "$taskTitle" from "$fromState" to "$toState""""
                }

                ActionType.PROJECT_CREATED -> """created the project "${project.name}""""

                ActionType.PROJECT_TITLE_MODIFIED -> {
                    """modified the project "${project.name}""""
                }

                ActionType.PROJECT_DELETED -> """deleted the project "${project.name}""""

                ActionType.TASK_DESCRIPTION_MODIFIED -> {
                    "modified the task description ${project.name}"
                }

                ActionType.TASK_ASSIGNED_USER_MODIFIED -> {
                    "changed task assignment"
                }

                ActionType.PROJECT_ASSIGNED_USER -> {
                    ""
                }

                ActionType.PROJECT_UNASSIGNED_USER -> {
                    ""
                }

                ActionType.TASK_TITLE_MODIFIED -> {
                    val taskTitle = taskMap[log.taskId]?.title ?: "Unknown Task"
                    val fromState = log.currentState ?: "Unknown"
                    val toState = log.newState ?: "Unknown"
                    """moved the task "$taskTitle" from "$fromState" to "$toState""""
                }
            }
            println("🕒 [$actionDate] ${user.username} $actionDescription")
        }
    }

    private fun viewProjectSwimlaneView(allowedStates: List<TaskState>, projectId: UUID) {
        val projectTasks = manageTask.getProjectTasks(projectId)

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

    private fun editProject(projectId: UUID, currentUser: User) {
        val updatedProject = manageProject.getProject(projectId)

        display.displayMenu(
            listOf(
                UiAction("Title", { updateField("Title", updatedProject, currentUser = currentUser) }),
                UiAction("Assign Mate", { updateField("Assign Mate", updatedProject, currentUser = currentUser) }),
                UiAction("Remove Mate", { updateField("Remove Mate", updatedProject, currentUser = currentUser) }),
            ),
            menuTitle = "\nSelect the field you want to update:"
        )

        manageProject.updateProject(projectId, updatedProject)

        println("✅ Task updated successfully!")
    }

    private fun deleteTask(taskId: UUID) {
        try {
            val confirm = inputReader.readString("Are you sure you want to delete task $taskId? (yes/no): ")
            if (confirm.equals("yes", ignoreCase = true)) {
                manageTask.deleteTask(taskId)
                println("🗑️ Task $taskId deleted successfully!")
            } else {
                println("❌ Task deletion canceled.")
            }
        } catch (e: TaskNotFoundException) {
            println("Error: ${e.message}")
        }
    }

    private fun createTask(projectId: UUID, currentUser: User) {
        val title = inputReader.readString("Enter task title: ")
        val description = inputReader.readString("Enter task description: ")
        val project = manageProject.getProject(projectId)
        val assigneeInput: String
        var assigneeId: UUID? = null
        val mates = manageMate.getAllMates().filter { it.id in project.assignedMatesIds }

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

        val selectedStateIndex =
            inputReader.readInt("Select task state (1-${getCurrentStates().size}): ", 1, getCurrentStates().size)
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
            manageTask.createTask(newTask)
            println("✅ Task '${newTask.title}' created successfully!")
            viewProjectSwimlaneView(getCurrentStates(), projectId)
            manageAuditLog.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = projectId,
                    taskId = newTask.id,
                    actionType = ActionType.TASK_CREATED,
                    userId = currentUser.id,
                    currentState = getCurrentStates().toString(),
                    newState = newTask.title,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } catch (e: TaskAlreadyExistsException) {
            println("❌ Failed to create task: ${e.message}")
        }
    }

    private fun assignMateToProject(projectId: UUID, mateId: UUID) {
        if (manageMateAssignment.assign(projectId, mateId)) {
            println("👥 Mate assigned to project successfully!")
            manageAuditLog.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = projectId,
                    taskId = UUID.randomUUID(),
                    actionType = ActionType.PROJECT_ASSIGNED_USER,
                    userId = mateId,
                    currentState = History.NO_TASK_STATE.toString(),
                    newState = History.NO_UUID.toString(),
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } else {
            println("⚠️ Failed to assign mate.")
        }
    }

    private fun unassignMateFromProject(projectId: UUID, mateId: UUID) {
        if (manageMateAssignment.unAssign(projectId, mateId)) {
            println("👤 Mate unassigned from project successfully!")
            manageAuditLog.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = projectId,
                    taskId = UUID.randomUUID(),
                    actionType = ActionType.PROJECT_UNASSIGNED_USER,
                    userId = mateId,
                    currentState = History.NO_TASK_STATE.toString(),
                    newState = History.NO_UUID.toString(),
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
                    manageAuditLog.saveLog(
                        History(
                            id = UUID.randomUUID(),
                            projectId = updatedProject.id,
                            taskId = History.NO_UUID,
                            actionType = ActionType.PROJECT_TITLE_MODIFIED,
                            userId = currentUser.id,
                            currentState = History.NO_TASK_STATE.toString(),
                            newState = History.NO_UUID.toString(),
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
                manageAuditLog.saveLog(
                    History(
                        id = UUID.randomUUID(),
                        projectId = updatedProject.id,
                        taskId = History.NO_UUID,
                        actionType = ActionType.PROJECT_UNASSIGNED_USER,
                        userId = currentUser.id,
                        currentState = History.NO_TASK_STATE.toString(),
                        newState = History.NO_UUID.toString(),
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
            manageMate.getAllMates()
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