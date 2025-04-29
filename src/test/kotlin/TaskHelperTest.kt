import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.damascus.logic.model.Project
import org.damascus.logic.model.State
import org.damascus.logic.model.Task
import java.time.LocalDateTime
import org.damascus.TaskHelper
import org.damascus.logic.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TaskHelperTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskHelper: TaskHelper

    @BeforeEach
    fun setup() {
        taskRepository = mockk(relaxed = true)
        taskHelper = TaskHelper(taskRepository)
    }

    @Test
    fun `should return true when adding new Task to exist Project`() {
        //Given
        val newTask = Task(
            id = UUID.randomUUID(),
            title = "task manipulation",
            description = "create crud operation",
            state = State(id = UUID.randomUUID(), name = "In Progress"),
            projectId = fakeProjects.first().id,
            creationDate = LocalDateTime.now(),
        )
        every { taskRepository.createTask(task = newTask) } returns fakeProjects.any { it.id == newTask.projectId }

        //When
        val result = taskHelper.createTask(newTask)

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when adding new Task to not exist Project`() {
        //Given
        val newTask = Task(
            id = UUID.randomUUID(),
            title = "task manipulation",
            description = "create crud operation",
            state = State(id = UUID.randomUUID(), name = "In Progress"),
            projectId = UUID.randomUUID(),
            creationDate = LocalDateTime.now()
        )
        every { taskRepository.createTask(task = newTask) } returns fakeProjects.any { it.id == newTask.projectId }

        //When
        val result = taskHelper.createTask(newTask)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when create task that already exist`() {
        //Given
        val task = fakeTasks.first()
        every { taskRepository.createTask(task = task) } returns fakeTasks.contains(task)

        //When
        val result = taskHelper.createTask(task)

        //Then
        assertThat(result).isTrue()

    }

    @Test
    fun `should return true when create task that not exist`() {
        //Given
        val task = Task(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            title = "Task 4",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = LocalDateTime.now()
        )
        every { taskRepository.createTask(task = task) } returns !fakeTasks.contains(task)

        //When
        val result = taskHelper.createTask(task)

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when update task that is exist`() {
        //Given
        val task = fakeTasks.first()
        every { taskRepository.updateTask(taskId = task.id, task = task) } returns fakeTasks.contains(task)

        //When
        val result = taskHelper.updateTask(task.id, task)

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when update task that not exist`() {
        //Given
        val task = Task(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            title = "Task 10",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = LocalDateTime.now()
        )
        every { taskRepository.updateTask(taskId = task.id, task = task) } returns fakeTasks.contains(task)

        //When
        val result = taskHelper.updateTask(taskId = task.id, task = task)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when delete task that not exist`() {
        //Given
        val task = Task(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            title = "Task 4",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = LocalDateTime.now()
        )
        every { taskRepository.deleteTask(taskId = task.id) } returns fakeTasks.contains(task)

        //When
        val result = taskHelper.deleteTask(task.id)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when delete task that is exist`() {
        //Given
        val task = fakeTasks.first()
        every { taskRepository.deleteTask(taskId = task.id) } returns fakeTasks.contains(task)

        //When
        val result = taskHelper.deleteTask(task.id)

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return tasks when tasks list is not empty according to project id`() {
        //Given
        every { taskRepository.getTasksByProject(fakeProjects.first().id) } returns fakeTasks
            .filter { it.projectId == fakeProjects.first().id }

        //When
        val result = taskHelper.getTasksByProject(fakeProjects.first().id)

        //Then
        assertThat(result.map { it.title }).containsExactly(
            "Task 1",
            "Task 2"
        )
    }

    @Test
    fun `should return empty list when tasks list is empty according to project id`() {
        //Given
        every { taskRepository.getTasksByProject(projectId = UUID.randomUUID()) } returns fakeTasks

        //When
        val result = taskHelper.getTasksByProject(projectId = UUID.randomUUID())

        //Then
        assertThat(result).isEmpty()
    }

    private val fakeProjects = mutableListOf(
        Project(id = UUID.randomUUID(), name = "Project 1", creationDate = LocalDateTime.now()),
        Project(id = UUID.randomUUID(), name = "Project 2", creationDate = LocalDateTime.now()),
        Project(id = UUID.randomUUID(), name = "Project 3", creationDate = LocalDateTime.now())
    )

    private val fakeTasks = mutableListOf(
        Task(
            id = UUID.randomUUID(),
            projectId = fakeProjects.first().id,
            title = "Task 1",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = LocalDateTime.now()
        ),

        Task(
            id = UUID.randomUUID(),
            projectId = fakeProjects.first().id,
            title = "Task 2",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = LocalDateTime.now()
        ),

        Task(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            title = "Task 3",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = LocalDateTime.now()
        )
    )

}