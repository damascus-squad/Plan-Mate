package org.damascus.ui.views.taskState

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskStateRepository
import java.util.*
import kotlin.test.Test

class GetAllTaskStateUiTest {

    private val repository = mockk<TaskStateRepository>()
    private val ui = GetAllTaskStateUi(repository)

    @Test
    fun `should print message when no states exist`() = runTest {
        coEvery { repository.getAllStates() } returns emptyList()
        ui()
    }

    @Test
    fun `should display table with state names and project references`() = runTest {
        coEvery { repository.getAllStates() } returns listOf(
            TaskState(UUID.randomUUID(), "To Do", 1),
            TaskState(UUID.randomUUID(), "In Progress", 1)
        )

        ui()
    }

    @Test
    fun `buildHeaders returns correct header list`() {
        val states = listOf(
            TaskState(UUID.randomUUID(), "Open", 0),
            TaskState(UUID.randomUUID(), "Closed", 0)
        )
        val headers = ui.buildHeaders(states)
        assertEquals(listOf("State Name", "Open", "Closed"), headers)
    }

    @Test
    fun `buildUsageRow returns correct usage row`() {
        val states = listOf(
            TaskState(UUID.randomUUID(), "Open", 1),
            TaskState(UUID.randomUUID(), "Closed", 3)
        )
        val usageRow = ui.buildUsageRow(states)
        assertEquals(listOf("Used In", "1 project", "3 projects"), usageRow)
    }

}
