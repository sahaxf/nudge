package focus.domain

import kotlinx.datetime.Instant

/**
 * A focus task with a title, duration, priority, and lifecycle status.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val durationMinutes: Int,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val createdAt: Instant,
    val completedAt: Instant? = null,
    val tag: String = "Deep Work"
)

