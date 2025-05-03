package presentation

import io.mockk.*
import logic.usecase.util.NoLogsException
import org.damascus.logic.usecase.AuditLogUseCase
import org.damascus.ui.io.InputReader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import org.damascus.logic.model.History
import org.damascus.ui.views.AuditLogView

class AuditLogViewTest {

 private lateinit var auditUseCase: AuditLogUseCase
 private lateinit var inputReader: InputReader
 private lateinit var auditLogView: AuditLogView

 @BeforeEach
 fun setUp() {
  auditUseCase = mockk(relaxed = true)
  inputReader = mockk()
  auditLogView = AuditLogView(auditUseCase, inputReader)
 }

 @Test
 fun `should call getLogsByProjectId when 'project' pass`() {
  val uuid = UUID.randomUUID()
  val fakeHistory = mockk<History>()

  every { inputReader.readString(any()) } returns uuid.toString()
  every { auditUseCase.getLogsByProjectId(uuid) } returns listOf(fakeHistory)

  auditLogView.runAuditLogView("project")

  verify { auditUseCase.getLogsByProjectId(uuid) }
 }

 @Test
 fun `should call getLogByTaskId when 'task' passed`() {

  val uuid = UUID.randomUUID()
  val fakeHistory = mockk<History>()

  every { inputReader.readString(any()) } returns uuid.toString()
  every { auditUseCase.getLogByTaskId(uuid) } returns listOf(fakeHistory)

  auditLogView.runAuditLogView("task")

  verify { auditUseCase.getLogByTaskId(uuid) }
 }

 @Test
 fun `should handle case-insensitive input for project`() {
  val uuid = UUID.randomUUID()
  every { inputReader.readString(any()) } returns uuid.toString()
  every { auditUseCase.getLogsByProjectId(uuid) } returns listOf(mockk())

  auditLogView.runAuditLogView("ProJect")

  verify { auditUseCase.getLogsByProjectId(uuid) }
 }

 @Test
 fun `should display error when NoLogsException is thrown for project`() {
  val uuid = UUID.randomUUID()

  every { inputReader.readString(any()) } returns uuid.toString()
  every { auditUseCase.getLogsByProjectId(uuid) } throws NoLogsException("No logs founds")

  auditLogView.runAuditLogView("project")

  verify { auditUseCase.getLogsByProjectId(uuid) }
 }

 @Test
 fun `should display error when NoLogsException is thrown for task`() {
  val uuid = UUID.randomUUID()

  every { inputReader.readString(any()) } returns uuid.toString()
  every { auditUseCase.getLogByTaskId(uuid) } throws NoLogsException("No logs")

  auditLogView.runAuditLogView("task")

  verify { auditUseCase.getLogByTaskId(uuid) }
 }

 @Test
 fun `should display error on invalid UUID for project`() {

  every { inputReader.readString(any()) } returns "not-uuid"

  auditLogView.runAuditLogView("project")

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should display error on invalid UUID for task`() {

  every { inputReader.readString(any()) } returns "not-uuid"

  auditLogView.runAuditLogView("task")

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should display error message when there is invalid choice`() {

  auditLogView.runAuditLogView("invalid")

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should quit early when user types 'quit' at project ID prompt`() {

  every { inputReader.readString(any()) } returns "quit"

  auditLogView.runAuditLogView("project")

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should quit when user types quit instead of ID in the task ID field`() {

  every { inputReader.readString(any()) } returns "exit"

  auditLogView.runAuditLogView("task")

  verify { auditUseCase wasNot Called }
 }
}
