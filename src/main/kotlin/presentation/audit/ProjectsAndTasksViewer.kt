package org.damascus.presentation.audit

import logic.usecase.util.NoLogsException

class projectsAndTasksViewer {
    private fun showProjects() {
        try {
            val projects = getProjectsAndTasks.getProjects()
            if (projects.isEmpty()) {
                println("⚠️ No projects found.")
                return
            }
            println("✅ Found ${projects.size} project(s):")
            projects.forEach { println("- ${it.name}") }
            val projectId = readValidatedIdOrQuit("Enter the project ID to view its log")
            if (projectId != null) showLogForProject(projectId)
        } catch (error: NoItemsFoundException) {
            println("⚠️ ${error.message}")
        }
    }

    private fun showTasks() {
        try {
            val tasks = getProjectsAndTasks.getTasks()
            if (tasks.isEmpty()) {
                println("⚠️ No tasks found.")
                return
            }
            println("✅ Found ${tasks.size} task(s):")
            tasks.forEach { println("- ${it.name}") }
            val taskId = readValidatedIdOrQuit("Enter the task ID to view its log")
            if (taskId != null) showLogForTask(taskId)
        } catch (error: NoLogsException) {
            println("⚠️ ${error.message}")
        }
    }
}