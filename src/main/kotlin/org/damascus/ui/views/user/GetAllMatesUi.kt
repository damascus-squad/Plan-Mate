package org.damascus.ui.views.user

import org.damascus.logic.exception.NoMatesAvailableException
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.printMateTable
import org.koin.core.annotation.Single

@Single
class GetAllMatesUi(
    private val display: Display,
    private val manageMateUseCase: ManageMateUseCase
) {

    operator suspend fun invoke() {
        try {
            val mates: List<User> = manageMateUseCase.getAllMates()
            if (mates.isNotEmpty()) {
                mates.printMateTable()
            }
        } catch (_: NoMatesAvailableException) {
            display.writeError("No mates found.")
        }
    }
}
