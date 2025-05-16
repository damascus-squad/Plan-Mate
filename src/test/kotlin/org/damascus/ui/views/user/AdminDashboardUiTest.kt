package org.damascus.ui.views.user

import io.mockk.mockk
import io.mockk.verify
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.views.project.AllProjectsUi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AdminDashboardUiTest {
    private lateinit var consoleDisplay: ConsoleDisplay
    private lateinit var allProjectUi: AllProjectsUi
    private lateinit var mateManagementUi: MateManagementUi
    private lateinit var adminDashboardUi: AdminDashboardUi


    @BeforeEach
    fun setup() {
        consoleDisplay = mockk(relaxed = true)
        allProjectUi = mockk(relaxed = true)
        mateManagementUi = mockk(relaxed = true)
        adminDashboardUi = AdminDashboardUi(consoleDisplay,allProjectUi,mateManagementUi)
    }

    @Test
    fun `showDashboard should accept an admin user`() {
        // Given
        val admin = User(
            id = UUID.randomUUID(),
            username = "notAdminUser",
            userRole = UserRole.ADMIN
        )

        // When
        adminDashboardUi(admin)

        // Then
        verify(exactly = 1) { consoleDisplay.displayMenu(any(), any()) }
    }

    private fun createUser(userRole: UserRole, username: String): User {
        return User(UUID.randomUUID(), username, userRole)
    }
}