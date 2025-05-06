package org.damascus.ui.views

import logic.model.User
import org.damascus.logic.model.Role
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.project.ProjectView
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle
import logic.usecase.auth.CreateMateUseCase
import org.damascus.ui.input.checkPasswordInput
import org.damascus.ui.input.checkUsernameInput


/* check :
1. password conditions
2. username conditions
3. CLI style
 */


class AdminDashboardView(
    private val inputReader: InputReader,
    private val projectView: ProjectView,
    private val createMateUseCase: CreateMateUseCase
) {
    fun showDashboard(user: User) {
        if (user.role != Role.ADMIN) {
            printMessageBox("Only admins can access this dashboard!", TerminalColor.Red)
            return
        }

        while (true) {
            printDashboardMenu()
            when (inputReader.readInt("Enter your choice: ", 1, 3)) {
                1 -> projectView.showAllProjects()
                2 -> showMateCreationView(user)
                3 -> return
            }
        }
    }

    private fun showMateCreationView(admin: User) {
        println("\n${SECTION_DIVIDER}")
        println("CREATE NEW MATE".withStyle(TerminalColor.Blue))
        println(SECTION_DIVIDER)

        println("Creating new mate, please fill the following fields")

        val usernameInput = getUsernameInput()
        val passwordInput = getPasswordInput()

        val result = createMateUseCase(admin = admin, newUsername = usernameInput, passwordInput)

        if (result.isSuccess) {
            printMessageBox("Successfully created mate: $usernameInput", TerminalColor.Green)
        } else {
            printMessageBox("Failed to create mate: ${result.exceptionOrNull()?.message ?: "Unknown error"}", TerminalColor.Red)
        }
    }

    private fun getUsernameInput(): String {
        return inputReader.readString("Enter username:").also { username ->
            if (!checkUsernameInput(username)) {
                return getUsernameInput()
            }
        }
    }

    private fun getPasswordInput(): String {
        return inputReader.readString("Enter password:").also { password ->
            if (!checkPasswordInput(password)) {
                return getPasswordInput()
            }
        }
    }

    private fun printDashboardMenu() {
        println("\n$SECTION_DIVIDER")
        println("ADMIN DASHBOARD".withStyle(TerminalColor.Blue))
        println(SECTION_DIVIDER)
        println("1. See ALL Projects")
        println("2. Create New Mate")
        println("3. Exit Dashboard")
        println(SECTION_DIVIDER)
    }

    private fun printMessageBox(
        message: String,
        color: TerminalColor = TerminalColor.Green
    ) {
        val border = "=".repeat(message.length + 2).withStyle(color)
        println("╔$border╗")
        println("║ ${message.withStyle(color)} ║")
        println("╚$border╝")
    }

    private companion object {
        const val SECTION_DIVIDER = "=========================================="
    }
}