package org.damascus.presentation

import logic.domain.InputException
import org.damascus.presentation.io.ConsoleUserInput
import org.damascus.presentation.retrieve.PlanRetrieveUi

class PlanMateMoodUi(
    private val consoleUserInput: ConsoleUserInput,
    private val planRetrieveUi: PlanRetrieveUi,
) {

    fun start() {
        showMenu(
            uiActionList = listOf(
                UiAction(
                    name = "Projects",
                    action = {
                        val subActions = listOf(
                            UiAction("🔧 Create New Project", {
                                printScreenHeader("🛠️ Create Project")
                                planRetrieveUi.createProject()
                            }),
                            UiAction("📋 Show All Projects", {
                                printScreenHeader("📋 All Projects")
                                planRetrieveUi.displayProjects()
                            }),
                            UiAction("⚙️ Manage a Project", {
                                printScreenHeader("⚙️ Manage Project")
                                planRetrieveUi.manageProject()
                            })
                        )
                        showMenu(subActions)
                        printScreenHeader("🗂️ Projects Menu")
                    }
                )

            )
        )
    }
    private fun showMenu(uiActionList: List<UiAction>) {
        while (true) {
            print("=".repeat(40))
            print("\n🍽️ Welcome to Plan Mate App! 🍽️\n".withStyle(TerminalColor.Green))
            println("=".repeat(40))

            uiActionList.forEachIndexed { index, action ->
                val number = (index + 1).toString().padStart(2, '0')
                println("${number}. ${action.name}".withStyle(TerminalColor.entries.random()))
            }

            try {
                val input = consoleUserInput.readInt(
                    prompt = "\n👉 Enter your choice (0 to Exit): ".withStyle(TerminalColor.Yellow),
                    min = 0,
                    max = uiActionList.size
                )

                if (input == 0) {
                    println("\n👋 Exiting... Stay healthy!".withStyle(TerminalColor.Green))
                    return
                }

                println("\n✨ You selected: ${uiActionList[input - 1].name}".withStyle(TerminalColor.Cyan))
                uiActionList[input - 1].action()

            } catch (e: InputException) {
                println("⚠️ ${e.message}".withStyle(TerminalColor.Red))
            }

            println("\n🔄 Press Enter to return to menu...".withStyle(TerminalColor.Reset))
            readlnOrNull()
        }
    }
    fun printScreenHeader(title: String) {
        println("=".repeat(40))
        println("📌 $title".withStyle(TerminalColor.Cyan))
        println("=".repeat(40))
    }

}
