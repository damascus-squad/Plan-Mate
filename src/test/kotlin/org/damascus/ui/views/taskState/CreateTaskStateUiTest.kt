package org.damascus.ui.views.taskState

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.ui.io.InputReader
import kotlin.test.Test

class CreateTaskStateUiTest {

    private val inputReader = mockk<InputReader>()
    private val manageTaskState = mockk<ManageTaskStateUseCase>(relaxed = true)
    private val auditLogUseCase = mockk<ManageAuditLogUseCase>(relaxed = true)

    private val createTaskStateUi = CreateTaskStateUi(inputReader, manageTaskState, auditLogUseCase)

    @Test
    fun `should create task state and log audit`() = runTest{
        coEvery { inputReader.readString(any()) } returns "In Progress"

        createTaskStateUi()

        coVerify { manageTaskState.createTaskState("In Progress") }
        coVerify { auditLogUseCase.saveLog(any()) }
    }

}
