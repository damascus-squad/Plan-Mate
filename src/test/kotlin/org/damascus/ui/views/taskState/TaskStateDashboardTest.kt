package org.damascus.ui.views.taskState

import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class TaskStateDashboardTest {

    private val display: Display = mockk(relaxed = true)
    private val createStateUi: CreateTaskStateUi = mockk(relaxed = true)
    private val deleteStateUi: DeleteTaskStateUi = mockk(relaxed = true)
    private val updateStateUi: UpdateTaskStateUi = mockk(relaxed = true)
    private val getAllTaskStateUi: GetAllTaskStateUi = mockk(relaxed = true)

    private lateinit var dashboard: TaskStateDashboard

    @BeforeEach
    fun setUp() {
        dashboard = TaskStateDashboard(
            display = display,
            createStateUi = createStateUi,
            deleteStateUi = deleteStateUi,
            updateStateUi = updateStateUi,
            getAllTaskStateUi = getAllTaskStateUi
        )
    }

    @Test
    fun `should display menu with correct title`() {
        // Given
        val titleSlot = slot<String>()
        every { display.displayMenu(any(), capture(titleSlot)) } just Runs

        // When
        dashboard.invoke()

        // Then
        assertEquals("\n⚙️ Task State Management", titleSlot.captured)
    }

    @Test
    fun `menu actions - should be in correct order`() {
        // Given
        val slot = slot<List<UiAction>>()
        every { display.displayMenu(capture(slot), any()) } just Runs

        // When
        dashboard.invoke()

        // Then
        val actions = slot.captured
        val expectedOrder = listOf(
            "Show All States",
            "Create New State",
            "Delete State",
            "Update State"
        )

        assertEquals(expectedOrder, actions.map { it.name })
    }
}