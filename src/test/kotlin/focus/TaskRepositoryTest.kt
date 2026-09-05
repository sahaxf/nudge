package focus

import focus.data.TaskRepository
import focus.domain.Priority
import focus.domain.TaskStatus
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TaskRepositoryTest {

    @Test
    fun testCreateTaskAndToggleStatus() {
        runBlocking {
        val repo = TaskRepository()
        val task = repo.createTask(
            title = "Test planning UI",
            durationMinutes = 45,
            priority = Priority.HIGH,
            tag = "Deep Work"
        )

        assertNotNull(task)
        assertEquals("Test planning UI", task.title)
        assertEquals(45, task.durationMinutes)
        assertEquals("Deep Work", task.tag)
        assertEquals(TaskStatus.TODO, task.status)

        // Toggle status to completed
        val toggled = repo.toggleTaskStatus(task.id)
        assertEquals(TaskStatus.COMPLETED, toggled.status)
        assertNotNull(toggled.completedAt)

        // Toggle back to TODO
        val reverted = repo.toggleTaskStatus(task.id)
        assertEquals(TaskStatus.TODO, reverted.status)

        // Clean up
        repo.deleteTask(task.id)
        }
    }
}
