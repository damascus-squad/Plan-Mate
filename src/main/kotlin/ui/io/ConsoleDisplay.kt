package org.damascus.ui.io

import logic.model.Project
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.UiAction
import org.damascus.ui.util.printTable
import org.damascus.ui.util.withStyle

class ConsoleDisplay(
    private val inputReader: ConsoleUserInput
) : Display {
    override fun displayMenu(uiActionList: List<UiAction>, menuTitle: String) {
        while (true) {
            // Fancy banner with borders
            print("=".repeat(40))
            print("\n🍽️ $menuTitle 🍽️\n".withStyle(TerminalColor.Green))
            println("=".repeat(40))

            // Colorful menu items with padded numbers
            uiActionList.forEachIndexed { index, action ->
                val number = (index + 1).toString().padStart(2, '0')
                println("${number}. ${action.name}".withStyle(TerminalColor.entries.random()))
            }
            println("0. Exit".withStyle(TerminalColor.Yellow))

            try {
                // User-friendly input prompt
                val input = inputReader.readInt(
                    prompt = "\n👉 Enter your choice: ".withStyle(TerminalColor.Yellow),
                    min = 0,
                    max = uiActionList.size
                )

                if (input == 0) {
                    println("\n👋 Exiting...!".withStyle(TerminalColor.Green))
                    return
                }

                // Visual feedback on selection
                println("\n✨ You selected: ${uiActionList[input - 1].name}".withStyle(TerminalColor.Cyan))

                // Execute the selected action
                uiActionList[input - 1].action()

            } catch (e: Exception) {
                println("⚠️ ${e.message}".withStyle(TerminalColor.Red))
            }

            // Return to menu prompt
            println("\n🔄 Press Enter to return to menu...".withStyle(TerminalColor.Reset))
            readlnOrNull()
        }
    }

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


    fun printMessageBox(
        message: String,
        color: TerminalColor = TerminalColor.Green
    ) {
        val border = "=".repeat(message.length + 2).withStyle(color)
        println("╔$border╗")
        println("║ ${message.withStyle(color)} ║")
        println("╚$border╝")
    }
}