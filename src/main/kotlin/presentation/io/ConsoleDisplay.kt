package org.damascus.presentation.io

import logic.model.Project
import org.damascus.presentation.printTable

class ConsoleDisplay : Display {

    override fun displayAllProjects(
        projects: List<Project>,
        label: String,
        contentSelector: ((Project) -> Map<String, Any?>)?
    ) {
        if (projects.isEmpty()) {
            println("No projects to display.")
            return
        }

        println("📋 Displaying projects by $label:")

        val headers = if (contentSelector != null) {
            listOf("ID") + contentSelector(projects.first()).keys.toList()
        } else {
            listOf(
                "ID",
                "Name",
                "Assigned Mates",
                "Created Date"
            )
        }

        val data = projects.map { project ->
            val contentMap = contentSelector?.invoke(project)
            if (contentMap != null) {
                listOf(project.id.toString().take(8)) + headers.drop(1).map { contentMap[it] ?: "-" }
            } else {
                listOf(
                    project.id.toString().take(8),
                    project.name,
                    project.assignedMatesIds.size.toString(),
                    project.creationDate.toString()
                )
            }
        }

        printTable(headers, data)
    }

    override fun displayProjectsAsTable(projects: List<Project>) {
        if (projects.isEmpty()) {
            println("❌ No projects available.")
            return
        }

        val headers = listOf("No", "Project Name", "ID")
        val rows = projects.mapIndexed { index, project ->
            listOf(
                (index + 1).toString(),
                project.name,
                project.id.toString()
            )
        }

        printTable(headers, rows)
    }

}
