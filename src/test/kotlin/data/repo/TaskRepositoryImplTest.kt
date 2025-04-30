package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.Mate
import model.Project
import model.State
import model.Task
import org.damascus.data.repo.TaskRepositoryImpl
import org.damascus.data.source.TaskDataSource
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.NoSuchElementException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class TaskRepositoryImplTest {
    private lateinit var taskDataSource: TaskDataSource<Task>
    private lateinit var repository: TaskRepositoryImpl


    @BeforeEach
    fun setup() {
        taskDataSource = mockk(relaxed = true)
        repository = TaskRepositoryImpl(taskDataSource)
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
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        )

        every { taskDataSource.load() } returns fakeTasks
        every { taskDataSource.save(listOf(newTask)) } returns true
        //When
        val result = repository.create(newTask)

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
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
        every { taskDataSource.load() } returns emptyList()
        every { taskDataSource.save(listOf(newTask)) } returns false
        //When
        val result = repository.create(newTask)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when create task that already exist`() {
        //Given
        val existingTask = fakeTasks.first()
        every { taskDataSource.load() } returns fakeTasks

        //When
        val result = repository.create(existingTask)

        //Then
        assertThat(result).isFalse()

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
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
        every { taskDataSource.load() } returns fakeTasks
        every { taskDataSource.save(any()) } returns true

        //When
        val result = repository.create(task)

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when update task that is exist`() {
        //Given
        val existingTask = fakeTasks.first()
        val updatedTask = existingTask.copy(title = "Updated Task")

        every { taskDataSource.load() } returns fakeTasks.toList()
        every { taskDataSource.save(any()) } returns true

        //When
        val result = repository.update(existingTask.id, updatedTask)

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
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        every { taskDataSource.load() } returns emptyList()
        every { taskDataSource.save(any()) } returns false

        //When
        val result = repository.update(taskId = task.id, task = task)

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
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
        every { taskDataSource.save(listOf(task)) } returns false

        //When
        val result = repository.delete(task.id)

        //Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when delete task that is exist`() {
        //Given
        val existingTask = fakeTasks.first()

        every { taskDataSource.load() } returns fakeTasks.toList()
        every { taskDataSource.save(any()) } returns true

        //When
        val result = repository.delete(existingTask.id)

        //Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return tasks when tasks list is not empty according to project id`() {
        //Given
        val projectId = fakeProjects.first().id

        every { taskDataSource.load() } returns fakeTasks

        //When
        val result = repository.getByProject(projectId)

        //Then
        val expectedTasks = fakeTasks.filter { it.projectId == projectId }
        assertThat(result).containsExactlyElementsIn(expectedTasks)
    }

    @Test
    fun `should return empty list when tasks list is empty according to project id`() {
        //Given
        every { taskDataSource.load() } returns fakeTasks

        //When
        val result = repository.getByProject(UUID.randomUUID())

        //Then
        assertThat(result).isEmpty()

    }

    @Test
    fun `should return task when given task id`() {
        //Given
        val existingId = fakeTasks.first().id
        every { taskDataSource.load() } returns fakeTasks

        //When
        val result = repository.get(existingId)

        //Then
        assertThat(result).isIn(fakeTasks)

    }

    @Test
    fun `should throws exception when task id is not found`() {
        //Given
        every { taskDataSource.load() } returns fakeTasks

        //When && Then
        assertThrows<NoSuchElementException> {
            repository.get(UUID.randomUUID())
        }

    }

    private val fakeProjects = mutableListOf(
        Project(
            id = UUID.randomUUID(),
            name = "Project 1",
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            assignedMates = mutableListOf( Mate(id = UUID.randomUUID(), username = "mate 1", password = "########"))
        ),
        Project(
            id = UUID.randomUUID(),
            name = "Project 2",
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            assignedMates = mutableListOf( Mate(id = UUID.randomUUID(), username = "mate 2", password = "########"))
        ),
    )

    private val fakeTasks = mutableListOf(
        Task(
            id = UUID.randomUUID(),
            projectId = fakeProjects.first().id,
            title = "Task 1",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        ),

        Task(
            id = UUID.randomUUID(),
            projectId = fakeProjects.first().id,
            title = "Task 2",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        ),

        Task(
            id = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            title = "Task 3",
            description = "des",
            state = State(id = UUID.randomUUID(), name = "Done"),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    )
}