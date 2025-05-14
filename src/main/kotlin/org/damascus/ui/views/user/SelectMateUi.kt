package org.damascus.ui.views.user

import org.damascus.logic.model.User
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.util.printMateTable

class SelectMateUi(
    private val inputReader: InputReader,
    private val display: Display
) {

    operator fun invoke(mates: List<User>): User? {
        if (mates.isEmpty()) {
            display.writeError(errorMessage = "No mates assigned to this project.")
            return null
        }

        display.write(prompt = "\n👥 Available Mates:")

        mates.printMateTable()

        val selected = inputReader.readInt("Enter the number of the mate (or 0 to skip)")
        if (selected == 0) return null

        return mates[selected - 1]
    }
}