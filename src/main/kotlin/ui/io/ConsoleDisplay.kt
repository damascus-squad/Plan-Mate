package ui.io

import ui.util.TerminalColor
import ui.util.UiAction
import ui.util.withStyle
import kotlin.system.exitProcess

class ConsoleDisplay(
    private val inputReader: InputReader
) : Display {
    override fun displayMenu(
        uiActionList: List<UiAction>,
        menuTitle: String,
        showBackOption: Boolean
    ){
        while (true) {
            // Fancy banner with borders
            print("=".repeat(40))
            print("\n $menuTitle \n".withStyle(TerminalColor.Green))
            println("=".repeat(40))

            // Colorful menu items with padded numbers
            uiActionList.forEachIndexed { index, action ->
                val number = (index + 1).toString().padStart(2, '0')
                val colorsExcludingRed = TerminalColor.entries.filter { it != TerminalColor.Red }
                println("${number}. ${action.name}".withStyle(colorsExcludingRed.random()))
            }

            if (showBackOption) {
                println("00. ⬅️ Back".withStyle(TerminalColor.Red))
            } else {
                println("00. ❌ Exit".withStyle(TerminalColor.Red))
            }

            try {
                // User-friendly input prompt
                val input = inputReader.readInt(
                    prompt = " Enter your choice" .withStyle(TerminalColor.Yellow),
                    min = 0,
                    max = uiActionList.size
                )

                if (input == 0) {
                    if (!showBackOption) {
                        println("\n👋 Exiting...!".withStyle(TerminalColor.Green))
                        exitProcess(0)
                    } else {
                        return
                    }
                }

                // Visual feedback on selection
                println("\n✨ You selected: ${uiActionList[input - 1].name}".withStyle(TerminalColor.Cyan))

                // Execute the selected action
                uiActionList[input - 1].action()

            } catch (e: Exception) {
                println("⚠️ ${e.message}".withStyle(TerminalColor.Red))
            }
        }
    }

    override fun write(prompt: String) {
        println(prompt.withStyle(TerminalColor.Blue))
    }

    override fun writeError(errorMessage: String) {
        println("❌ $errorMessage".withStyle(TerminalColor.Red))
    }
}