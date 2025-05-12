package org.damascus.ui.views.user

import logic.exception.NoMatesAvailableException
import logic.model.User
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import ui.io.Display
import ui.util.printMateTable

class GetAllMatesUi(
    private val display: Display,
    private val getAllMatesUseCase: GetAllMatesUseCase
) {

    operator fun invoke() {
        try {
            val mates: List<User> = getAllMatesUseCase()
            if (mates.isNotEmpty()) {
                mates.printMateTable()
            }
        } catch (e: NoMatesAvailableException) {
            display.writeError("No mates found.")
        }
    }
}
