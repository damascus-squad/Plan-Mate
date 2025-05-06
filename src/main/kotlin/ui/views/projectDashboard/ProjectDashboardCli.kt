package org.damascus.ui.views.projectDashboard

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.NoLogException
import logic.exception.TaskAlreadyExistsException
import logic.exception.TaskNotFoundException
import logic.model.Project
import logic.model.Task
import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.auditLog.GetLogsByTaskIdUseCase
import logic.usecase.project.ModifyMateAssignmentUseCase
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.usecase.AuditLog.SaveLogUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.UpdateProjectUseCase
import org.damascus.logic.usecase.task.CreateTaskUseCase
import org.damascus.logic.usecase.task.DeleteTaskUseCase
import org.damascus.logic.usecase.task.GetTasksByProjectUseCase
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.util.UiAction
import java.util.*


class ProjectDashboardCli(
    private val display: ConsoleDisplay,
    private val inputReader: ConsoleUserInput,
    private val getProjectUseCase: GetProjectUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val getTaskStateByIdUseCase:GetTaskStateByIdUseCase,
    private val getLogsByProjectIdUseCase: GetLogsByProjectIdUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val getLogsByTaskIdUseCase: GetLogsByTaskIdUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val modifyMateAssignmentUseCase: ModifyMateAssignmentUseCase
) : ProjectDashboardController {

    //TODO
    fun start(projectId: UUID) {
        viewProjectSwimlaneView(projectId)
        display.displayMenu(
            listOf(
                UiAction("Edit Project") { editProject(projectId) },
            ),
            menuTitle = "Project Dashboard"
        )
    }


    fun viewProjectSwimlaneView(projectId: UUID) {
        val projectTasks = getTasksByProjectUseCase(projectId)

        val todoTasks = mutableListOf<String>()
        val inProgressTasks = mutableListOf<String>()
        val doneTasks = mutableListOf<String>()

        projectTasks.forEach { task ->
            // TODO : import GetTaskStateByIdUseCase
            when (getTaskStateByIdUseCase(task.stateId)) {
                "TODO" -> todoTasks.add(task.title)
                "IN_PROGRESS"-> inProgressTasks.add(task.title)
                "DONE" -> doneTasks.add(task.title)
            }
        }

        val swimlaneRepresentation = StringBuilder()
        swimlaneRepresentation.append(" Project Dashboard\n")
        swimlaneRepresentation.append("-----------------------------------------------------------\n")
        swimlaneRepresentation.append("TODO                  | In Progress |                   Done\n")
        swimlaneRepresentation.append("-----------------------------------------------------------\n")

        val maxRows = maxOf(todoTasks.size, inProgressTasks.size, doneTasks.size)

        for (i in 0 until maxRows) {
            swimlaneRepresentation.append(
                "${todoTasks.getOrNull(i) ?: " "}".padEnd(25) +
                        "| ${inProgressTasks.getOrNull(i) ?: " "}".padEnd(23) +
                        "| ${doneTasks.getOrNull(i) ?: " "}".padEnd(23) + "\n"
            )
        }
        print(swimlaneRepresentation.toString())
    }

    override fun editProject(projectId: UUID) {
        val updatedProject = getProjectUseCase(projectId)

        display.displayMenu(
            listOf(
                UiAction("Title") { updateField("Title", updatedProject) },
                UiAction("Assign Mate") { updateField("Assign Mate", updatedProject) }
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

    override fun createTask(projectId: UUID, task: Task) {
        val title = inputReader.readString("Enter task title: ")
        val description = inputReader.readString("Enter task description: ")
        //TODO : mate name
        val assigneeInput = inputReader.readString("Enter assignee ID (leave blank if none): ")
        //TODO : state name
        val stateInput = inputReader.readString("Enter task state ID: ")

        val assigneeId = if (assigneeInput.isNotBlank()) UUID.fromString(assigneeInput) else null
        val stateId = UUID.fromString(stateInput)

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


    override fun assignMateToProject(projectId: UUID, mateId: UUID, shouldAssign: Boolean) {
        try {
            val success = modifyMateAssignmentUseCase(projectId, mateId, shouldAssign)
            if (success) {
                val action = if (shouldAssign) "assigned to" else "removed from"
                println("👥 Mate $action project successfully!")
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
                println("⚠️ Failed to ${if (shouldAssign) "assign" else "remove"} mate.")
            }
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
        }
    }

    override fun viewProjectHistory(projectId: UUID) {
        try {
            val projectLogs = getLogsByProjectIdUseCase(projectId)

            println("\n📜 Project History:")

            projectLogs.forEach { log ->
                println("Action: ${log.actionType} by User ${log.userId} on ${log.actionDate}")
                println("From state: ${log.currentStateId} to state: ${log.newStateId}")
            }

        } catch (e: NoLogException) {
            println("Error retrieving project history: ${e.message}")
        }
    }


    override fun viewTaskHistory(projectId: UUID) {
        try {
            val taskLogs = getLogsByTaskIdUseCase(projectId)
            println("\n📜 Task History for this project:")
            taskLogs.forEach { log ->
                println("Action: ${log.actionType} by User ${log.userId} on ${log.actionDate}")
                println("From state: ${log.currentStateId} to state: ${log.newStateId}")
            }
        } catch (e: NoLogException) {
            println("Error retrieving task history: ${e.message}")
        }
    }

    private fun updateField(fieldName: String, updatedProject: Project): Project {
        println("You selected: $fieldName")
        when (fieldName) {
            "Title" -> {
                val newTitle = inputReader.readString("Enter new title (leave blank to keep existing): ")
                if (newTitle.isNotBlank()) {
                    saveLogUseCase(
                        History(
                            id = UUID.randomUUID(),
                            projectId = updatedProject.id,
                            taskId = History.NO_UUID,
                            actionType = ActionType.PROJECT_MODIFIED,
                            //TODO : add user id
                            userId = UUID.randomUUID(),
                            currentStateId = History.NO_UUID,
                            newStateId = History.NO_UUID,
                            actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        )
                    )
                    return updatedProject.copy(name = newTitle)
                } else {
                    println("No change to title.")
                    return updatedProject
                }
            }

            "Assign Mate" -> {
                //TODO: mate name
                val mateIdStr = inputReader.readString("Enter mate ID to assign/remove: ")
                try {
                    val mateId = UUID.fromString(mateIdStr)
                    val action = inputReader.readString("Type 'assign' to assign or 'remove' to unassign: ").lowercase()

                    val shouldAssign = when (action) {
                        "assign" -> true
                        "remove" -> false
                        else -> {
                            println("Invalid action. Use 'assign' or 'remove'.")
                            return updatedProject
                        }
                    }
                    assignMateToProject(updatedProject.id, mateId, shouldAssign)
                    println("Mate $action successfully.")
                    return updatedProject
                } catch (e: IllegalArgumentException) {
                    println("Error: ${e.message}.")
                    return updatedProject
                }
            }

            else -> return updatedProject
        }
    }

}