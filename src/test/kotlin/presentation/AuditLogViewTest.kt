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
 fun `should call getLogsByProjectId when user selects project`() {
  val uuid = UUID.randomUUID()
  val fakeHistory = mockk<History>()

  every { inputReader.readString(any()) } returnsMany listOf("1", uuid.toString())
  every { auditUseCase.getLogsByProjectId(uuid) } returns listOf(fakeHistory)

  auditLogView.runAuditLogView()

  verify { auditUseCase.getLogsByProjectId(uuid) }
 }

 @Test
 fun `should call getLogByTaskId when user selects task`() {

  val uuid = UUID.randomUUID()
  val fakeHistory = mockk<History>()

  every { inputReader.readString(any()) } returnsMany listOf("2", uuid.toString())
  every { auditUseCase.getLogByTaskId(uuid) } returns listOf(fakeHistory)

  auditLogView.runAuditLogView()

  verify { auditUseCase.getLogByTaskId(uuid) }
 }

 @Test
 fun `should not call use case when user quits`() {

  every { inputReader.readString(any()) } returns "quit"

  auditLogView.runAuditLogView()

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should not crash on NoLogsException for project`() {
  val uuid = UUID.randomUUID()

  every { inputReader.readString(any()) } returnsMany listOf("1", uuid.toString())
  every { auditUseCase.getLogsByProjectId(uuid) } throws NoLogsException("No logs")

  auditLogView.runAuditLogView()

  verify { auditUseCase.getLogsByProjectId(uuid) }
 }

 @Test
 fun `should not crash on NoLogsException for task`() {
  val uuid = UUID.randomUUID()

  every { inputReader.readString(any()) } returnsMany listOf("2", uuid.toString())
  every { auditUseCase.getLogByTaskId(uuid) } throws NoLogsException("No logs")

  auditLogView.runAuditLogView()

  verify { auditUseCase.getLogByTaskId(uuid) }
 }

 @Test
 fun `should display error on invalid UUID for project`() {

  every { inputReader.readString(any()) } returnsMany listOf("1", "not-uuid")

  auditLogView.runAuditLogView()

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should display error on invalid UUID for task`() {

  every { inputReader.readString(any()) } returnsMany listOf("2", "not-uuid")

  auditLogView.runAuditLogView()

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should display error on invalid menu choice`() {

  every { inputReader.readString(any()) } returns "invalid-option"

  auditLogView.runAuditLogView()

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should quit early when user types quit at project ID prompt`() {

  every { inputReader.readString(any()) } returnsMany listOf("1", "quit")

  auditLogView.runAuditLogView()

  verify { auditUseCase wasNot Called }
 }

 @Test
 fun `should quit early when user types quit at task ID prompt`() {

  every { inputReader.readString(any()) } returnsMany listOf("2", "exit")

  auditLogView.runAuditLogView()

  verify { auditUseCase wasNot Called }
 }

}
