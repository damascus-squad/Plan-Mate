package org.damascus.ui.views.user

import logic.model.User
import ui.io.Display
import ui.io.InputReader
import ui.util.printTable

class SelectMateUi(
    private val inputReader: InputReader,
    private val display: Display
) {

    operator fun invoke(mates: List<User>): User {
        var selectedIndex = 0
        if (mates.isEmpty()) {
            display.writeError(errorMessage = " No mates assigned to this project.")
        } else {
            display.write(prompt = "\n👥 Available Mates:")
            val headers = listOf("ID", "Name")
            val rows = mates.mapIndexed { index, mate ->
                listOf((index + 1).toString(), mate.username)
            }
            printTable(headers, rows)

           selectedIndex = inputReader.readInt(
                prompt = "Enter the number of the mate to assign: ",
                min = 1,
                max = mates.size
            )
        }
        return mates[selectedIndex - 1]
    }
}