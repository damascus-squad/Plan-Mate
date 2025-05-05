package org.damascus.ui.io

import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.UiAction
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
}