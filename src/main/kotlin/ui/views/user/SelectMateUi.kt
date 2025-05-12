package org.damascus.ui.views.user

import logic.model.User
import ui.io.Display
import ui.io.InputReader
import ui.util.printTable

class SelectMateUi(
    private val inputReader: InputReader,
    private val display: Display
) {

    operator fun invoke(mates: List<User>): User? {
        if (mates.isEmpty()) {
            display.writeError(errorMessage = "No mates assigned to this project.")
            return null
        }

        display.write("\n👥 Available Mates:")
        val headers = listOf("ID", "Name")
        val rows = mates.mapIndexed { index, mate ->
            listOf((index + 1).toString(), mate.username)
        }
        printTable(headers, rows)

        val selected = inputReader.readInt("Enter the number of the mate (or 0 to skip)")
        if (selected == 0) return null

        return mates[selected - 1]
    }
}