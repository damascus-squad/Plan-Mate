package org.damascus.ui.views

import logic.model.Admin
import logic.model.Project
import org.damascus.logic.model.Role
import org.damascus.logic.usecase.AuditLogUseCase
import org.damascus.logic.usecase.AuthenticationUseCase
import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.io.InputReader
import java.util.*
import org.damascus.logic.model.History
import org.damascus.logic.model.ActionType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.State
import logic.repo.TaskRepository
import logic.repo.TaskStateRepository
import org.damascus.logic.repository.ProjectRepository
import org.damascus.logic.usecase.ProjectUseCase.UpdateProjectUseCase

class AdminDashboard(
    private val authUseCase: AuthenticationUseCase,
    private val createProjectUseCase: CreateProjectUseCase,
    private val auditLogUseCase: AuditLogUseCase,
    private val display: Display,
    private val inputReader: InputReader,
    private val projectRepository: ProjectRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val taskStateRepository: TaskStateRepository,
    private val taskRepository: TaskRepository,

    ) {

    fun start() {
        val loginActions = listOf(
            UiAction("Login") { login() },
            UiAction("Exit") { }
        )

        display.displayMenu(loginActions, "Plan-Mate Login")
    }

    private fun login() {
        val username = inputReader.readString("Enter your username:")
        val password = inputReader.readString("Enter your password:")

        try {
            val userResult = authUseCase.login(username, password)
            userResult.onSuccess { user ->
                if (user.role == Role.ADMIN) {
                    showAdminMenu(user as Admin)
                } else {
                    val errorActions = listOf(
                        UiAction("Back to Login") { }
                    )
                    display.displayMenu(errorActions, "Error: User is not an admin")
                }
            }.onFailure {
                val errorActions = listOf(
                    UiAction("Back to Login") { val i = 0 } // i mean do nothing
                )
                display.displayMenu(errorActions, "Login Failed: ${it.message}")
            }
        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Back to Login") { Unit }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun showAdminMenu(admin: Admin) {
        val adminActions = listOf(
            UiAction("Create Mate") { handleCreateMate(admin) },
            UiAction("Create Project") { handleCreateProject(admin) },
            UiAction("Edit Project") { handleEditProject(admin) },
            UiAction("Delete Project") { handleDeleteProject(admin) },
            UiAction("Create State") { handleCreateState(admin) },
            UiAction("Edit State") { handleEditState(admin) },
            UiAction("Delete State") { handleDeleteState(admin) },
            UiAction("View Audit Logs") { handleViewAuditLogs(admin) }
        )

        display.displayMenu(adminActions, "Admin Dashboard")
    }

    private fun handleCreateMate(admin: Admin) {
        val username = inputReader.readString("Enter username for new mate:")
        val password = inputReader.readString("Enter password for new mate:")

        try {
            val result = authUseCase.createMate(admin, username, password)

            if (result.isSuccess) {
                val successActions = listOf(
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "Mate created successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleCreateMate(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to create mate: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleCreateMate(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun handleCreateProject(admin: Admin) {
        val projectName = inputReader.readString("Enter project name:")

        if (projectName.isEmpty()) {
            val errorActions = listOf(
                UiAction("Try Again") { handleCreateProject(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: Project name cannot be empty")
            return
        }

        try {
            val newProject = Project(
                id = UUID.randomUUID(),
                name = projectName,
                assignedMatesIds = mutableListOf(),
                creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            val result = createProjectUseCase.invoke(newProject)

            if (result) {
                try {
                    logProjectCreation(admin, newProject)
                } catch (e: Exception) {
                    val logErrorActions = listOf(
                        UiAction("Continue") { }
                    )
                    display.displayMenu(logErrorActions, "Error logging project creation: ${e.message}")
                }

                val successActions = listOf(
                    UiAction("Create Another Project") { handleCreateProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "Project '${newProject.name}' created successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleCreateProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to create project. A project with this ID may already exist.")
            }
        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleCreateProject(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error creating project: ${e.message}")
        }
    }

    private fun handleEditProject(admin: Admin) {
        val projectId = inputReader.readString("Enter the project ID to edit:")

        try {
            val allProjects = projectRepository.getAll()

            val project = allProjects.find { it.id == UUID.fromString(projectId) }

            if (project == null) {
                val errorActions = listOf(
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Error: Project with ID '$projectId' not found.")
                return
            }

            println("Current Project Name: ${project.name}")

            val newProjectName = inputReader.readString("Enter the new project name:")

            if (newProjectName.isEmpty()) {
                val errorActions = listOf(
                    UiAction("Try Again") { handleEditProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Error: Project name cannot be empty")
                return
            }

            val updatedProject = project.copy(name = newProjectName)

            val result = updateProjectUseCase.invoke(projectId = UUID.fromString(projectId), project = updatedProject)

            if (result) {
                val successActions = listOf(
                    UiAction("Edit Another Project") { handleEditProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "Project '${updatedProject.name}' updated successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleEditProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to update project.")
            }

        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleEditProject(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun handleDeleteProject(admin: Admin) {
        val projectId = inputReader.readString("Enter the project ID to delete:")

        try {
            val project = projectRepository.get(UUID.fromString(projectId))

            val result = projectRepository.delete(projectId = UUID.fromString(projectId))

            if (result) {
                val successActions = listOf(
                    UiAction("Delete Another Project") { handleDeleteProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "Project '${project.name}' deleted successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleDeleteProject(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to delete project.")
            }

        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleDeleteProject(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun handleCreateState(admin: Admin) {
        val stateName = inputReader.readString("Enter state name:")

        if (stateName.isEmpty()) {
            val errorActions = listOf(
                UiAction("Try Again") { handleCreateState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "State name cannot be empty")
            return
        }

        try {
            val newState = State(
                id = UUID.randomUUID(),
                name = stateName
            )

            val result = taskStateRepository.create(newState)

            if (result) {
                val successActions = listOf(
                    UiAction("Create Another State") { handleCreateState(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "State '$stateName' created successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleCreateState(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to create state.")
            }
        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleCreateState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun handleEditState(admin: Admin) {
        val stateIdString = inputReader.readString("Enter state ID to edit:")
        val stateId = try {
            UUID.fromString(stateIdString)
        } catch (e: IllegalArgumentException) {
            val errorActions = listOf(
                UiAction("Try Again") { handleEditState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Invalid state ID format.")
            return
        }

        val existingState = taskStateRepository.getStateById(stateId)

        if (existingState == null) {
            val errorActions = listOf(
                UiAction("Try Again") { handleEditState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "State with the given ID does not exist.")
            return
        }

        val newStateName = inputReader.readString("Enter new name for the state:")
        if (newStateName.isEmpty()) {
            val errorActions = listOf(
                UiAction("Try Again") { handleEditState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "State name cannot be empty.")
            return
        }

        val updatedState = existingState.copy(name = newStateName)

        try {
            val result = taskStateRepository.update(updatedState)

            if (result) {
                val successActions = listOf(
                    UiAction("Edit Another State") { handleEditState(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "State '${updatedState.name}' edited successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleEditState(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to edit the state.")
            }
        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleEditState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun handleDeleteState(admin: Admin) {
        val stateIdString = inputReader.readString("Enter state ID to delete:")
        val stateId = try {
            UUID.fromString(stateIdString)
        } catch (e: IllegalArgumentException) {
            val errorActions = listOf(
                UiAction("Try Again") { handleDeleteState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Invalid state ID format.")
            return
        }

        val stateToDelete = taskStateRepository.getStateById(stateId)

        if (stateToDelete == null) {
            val errorActions = listOf(
                UiAction("Try Again") { handleDeleteState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "State with the given ID does not exist.")
            return
        }

        try {
            val result = taskStateRepository.delete(stateToDelete)

            if (result) {
                val successActions = listOf(
                    UiAction("Delete Another State") { handleDeleteState(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(successActions, "State '${stateToDelete.name}' deleted successfully!")
            } else {
                val errorActions = listOf(
                    UiAction("Try Again") { handleDeleteState(admin) },
                    UiAction("Back to Admin Menu") { }
                )
                display.displayMenu(errorActions, "Failed to delete the state.")
            }
        } catch (e: Exception) {
            val errorActions = listOf(
                UiAction("Try Again") { handleDeleteState(admin) },
                UiAction("Back to Admin Menu") { }
            )
            display.displayMenu(errorActions, "Error: ${e.message}")
        }
    }

    private fun logProjectCreation(admin: Admin, project: Project) {
        val history = History(
            id = UUID.randomUUID(),
            projectId = project.id,
            taskId = History.NO_UUID,
            actionType = ActionType.PROJECT_CREATED,
            userId = admin.id,
            currentStateId = History.NO_UUID,
            newStateId = History.NO_UUID,
            actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
        auditLogUseCase.saveLog(history)
    }

    private fun handleViewAuditLogs(admin: Admin) {
        val notImplementedActions = listOf(
            UiAction("Back to Admin Menu") { }
        )

        display.displayMenu(notImplementedActions, "View Audit Logs functionality not implemented yet")
    }

}