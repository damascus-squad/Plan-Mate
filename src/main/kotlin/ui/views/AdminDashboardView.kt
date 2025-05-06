package org.damascus.ui.views

import logic.model.Admin
import org.damascus.logic.model.Role
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.project.ProjectView
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle
import logic.usecase.auth.CreateMateUseCase


class AdminDashboardView(
    private val inputReader: InputReader,
    private val projectView: ProjectView,
    private val createMateUseCase: CreateMateUseCase
) {
    fun showDashboard(admin: Admin) {
        if (admin.role != Role.ADMIN) {
            println("Only admins can access this dashboard!")
            return
        }

        while (true) {
            printDashboardMenu()
            when (inputReader.readInt("Enter your choice: ", 1, 3)) {
                1 -> projectView.showAllProjects()
                2 -> viewMateCreation(admin, createMateUseCase)
                3 -> return
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

    private companion object {
        const val SECTION_DIVIDER = "=========================================="
    }
}