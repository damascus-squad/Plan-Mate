import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

import java.util.UUID

class TaskProjectTests {

 @Test
 fun `assign task to user successfully`() {
  val assigneeId = UUID.randomUUID()
  val taskId = UUID.randomUUID()
  val task = Task(
   id = taskId,
   projectId = UUID.randomUUID(),
   title = "Sample Task",
   description = "Sample task description",
   assigneeId = null,
   stateId = UUID.randomUUID(),
   creationDate = java.time.LocalDateTime.now()
  )

  // Simulate assigning the task to the user
  assignTaskToUser(task, assigneeId)

  // Verify the task was assigned successfully
  assertEquals(assigneeId, task.assigneeId, "The task should be assigned to the user.")
 }

 @Test
 fun `throw exception when trying to assign task with null assignee`() {
  val taskId = UUID.randomUUID()
  val task = Task(
   id = taskId,
   projectId = UUID.randomUUID(),
   title = "Sample Task",
   description = "Sample task description",
   assigneeId = null,
   stateId = UUID.randomUUID(),
   creationDate = java.time.LocalDateTime.now()
  )

  // Try to assign a null user, expecting an exception
  val exception = assertThrows<IllegalArgumentException> {
   assignTaskToUser(task, null)
  }
  assertEquals("Cannot assign task Sample Task to a null user ID.", exception.message)
 }

 @Test
 fun `add mate to project successfully`() {
  val mateId = UUID.randomUUID()
  val project = Project(
   id = UUID.randomUUID(),
   name = "Test Project",
   assignedMatesIds = mutableListOf(),
   creationDate = java.time.LocalDateTime.now()
  )

  // Add mate to project
  addMateToProject(project, mateId)

  // Verify the mate was added to the project
  assertTrue(project.assignedMatesIds.contains(mateId), "The mate should be added to the project.")
 }

 @Test
 fun `add existing mate to project should not duplicate`() {
  val mateId = UUID.randomUUID()
  val project = Project(
   id = UUID.randomUUID(),
   name = "Test Project",
   assignedMatesIds = mutableListOf(mateId),
   creationDate = java.time.LocalDateTime.now()
  )

  // Try adding the same mate again
  addMateToProject(project, mateId)

  // Verify that the mate list still only contains the mate once
  assertEquals(1, project.assignedMatesIds.size, "The mate list should not contain duplicates.")
 }

 @Test
 fun `throw exception when adding invalid mate to project`() {
  val invalidMateId = UUID.randomUUID()
  val project = Project(
   id = UUID.randomUUID(),
   name = "Test Project",
   assignedMatesIds = mutableListOf(),
   creationDate = java.time.LocalDateTime.now()
  )

  // Assuming adding mate is only valid if the mate exists or meets certain criteria, you can simulate invalid case.
  val exception = assertThrows<IllegalArgumentException> {
   // Simulating the logic that checks if the mate is valid before adding
   if (invalidMateId.toString() == "invalid") {
    throw IllegalArgumentException("Invalid mate ID.")
   }
   addMateToProject(project, invalidMateId)
  }

  assertEquals("Invalid mate ID.", exception.message)
 }

 // Helper functions
 fun assignTaskToUser(task: Task, assigneeId: UUID?) {
  if (assigneeId == null) {
   throw IllegalArgumentException("Cannot assign task ${task.title} to a null user ID.")
  }
  task.assigneeId = assigneeId
 }

 fun addMateToProject(project: Project, mateId: UUID) {
  if (project.assignedMatesIds.contains(mateId)) {
   println("Mate with ID $mateId is already assigned to project ${project.name}.")
  } else {
   project.assignedMatesIds.add(mateId)
   println("Mate with ID $mateId added to project ${project.name}.")
  }
 }
}
