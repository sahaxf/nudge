package focus.data

import focus.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * Repository for task and session persistence via SQLite.
 */
class TaskRepository {

    private val database: Database

    init {
        val dbDir = File(System.getProperty("user.home"), ".focushd")
        dbDir.mkdirs()
        val dbFile = File(dbDir, "focus.db")

        database = Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )

        transaction(database) {
            SchemaUtils.create(Tasks, FocusSessions)
            try {
                exec("ALTER TABLE tasks ADD COLUMN tag TEXT DEFAULT 'Deep Work'")
            } catch (_: Exception) {}

            // Remove any legacy mock tasks so by default the task list starts clean and empty
            Tasks.deleteWhere {
                (title eq "Research vector databases") or
                (title eq "Write blog post") or
                (title eq "Code review") or
                (title eq "Plan project roadmap") or
                (title eq "Reply to emails")
            }
        }
    }

    /**
     * Get active tasks (TODO + IN_PROGRESS), ordered by:
     * 1. Priority descending (HIGH > MEDIUM > LOW)
     * 2. Duration ascending
     * 3. Creation time ascending
     */
    suspend fun getActiveTasks(): List<Task> = withContext(Dispatchers.IO) {
        transaction(database) {
            Tasks.selectAll()
                .where {
                    (Tasks.status eq TaskStatus.TODO.name) or
                    (Tasks.status eq TaskStatus.IN_PROGRESS.name)
                }
                .map { it.toTask() }
                .sortedWith(
                    compareByDescending<Task> { it.priority.ordinal }
                        .thenBy { it.durationMinutes }
                        .thenBy { it.createdAt }
                )
        }
    }

    /**
     * Get all tasks for today (including completed), ordered by priority.
     */
    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        transaction(database) {
            Tasks.selectAll()
                .map { it.toTask() }
                .sortedWith(
                    compareByDescending<Task> { it.status != TaskStatus.COMPLETED }
                        .thenByDescending { it.priority.ordinal }
                        .thenBy { it.durationMinutes }
                        .thenBy { it.createdAt }
                )
        }
    }

    /**
     * Create a new task. Returns the task with its generated ID.
     */
    suspend fun createTask(
        title: String,
        durationMinutes: Int = 25,
        priority: Priority = Priority.MEDIUM,
        tag: String = "Deep Work"
    ): Task =
        withContext(Dispatchers.IO) {
            val now = Clock.System.now()
            val id = transaction(database) {
                Tasks.insert {
                    it[Tasks.title] = title
                    it[Tasks.durationMinutes] = durationMinutes
                    it[Tasks.priority] = priority.name
                    it[Tasks.status] = TaskStatus.TODO.name
                    it[Tasks.createdAt] = now.toString()
                    it[Tasks.completedAt] = null
                    it[Tasks.tag] = tag
                } get Tasks.id
            }
            Task(
                id = id,
                title = title,
                durationMinutes = durationMinutes,
                priority = priority,
                status = TaskStatus.TODO,
                createdAt = now,
                tag = tag
            )
        }

    /**
     * Update the status of a task.
     */
    suspend fun updateTaskStatus(taskId: Long, status: TaskStatus) = withContext(Dispatchers.IO) {
        transaction(database) {
            Tasks.update({ Tasks.id eq taskId }) {
                it[Tasks.status] = status.name
                if (status == TaskStatus.COMPLETED) {
                    it[Tasks.completedAt] = Clock.System.now().toString()
                } else {
                    it[Tasks.completedAt] = null
                }
            }
        }
    }

    /**
     * Toggle the status of a task between TODO and COMPLETED.
     */
    suspend fun toggleTaskStatus(taskId: Long): Task = withContext(Dispatchers.IO) {
        transaction(database) {
            val current = Tasks.selectAll().where { Tasks.id eq taskId }.firstOrNull()?.toTask()
                ?: error("Task not found: $taskId")
            val newStatus = if (current.status == TaskStatus.COMPLETED) TaskStatus.TODO else TaskStatus.COMPLETED
            val completedAtTime = if (newStatus == TaskStatus.COMPLETED) Clock.System.now().toString() else null
            Tasks.update({ Tasks.id eq taskId }) {
                it[Tasks.status] = newStatus.name
                it[Tasks.completedAt] = completedAtTime
            }
            current.copy(
                status = newStatus,
                completedAt = completedAtTime?.let { Instant.parse(it) }
            )
        }
    }

    /**
     * Delete a task by ID.
     */
    suspend fun deleteTask(taskId: Long) = withContext(Dispatchers.IO) {
        transaction(database) {
            Tasks.deleteWhere { id eq taskId }
        }
    }

    /**
     * Record a new focus session.
     */
    suspend fun createSession(taskId: Long, startedAt: Instant, plannedDurationSeconds: Int): Long =
        withContext(Dispatchers.IO) {
            transaction(database) {
                FocusSessions.insert {
                    it[FocusSessions.taskId] = taskId
                    it[FocusSessions.startedAt] = startedAt.toString()
                    it[FocusSessions.plannedDurationSeconds] = plannedDurationSeconds
                } get FocusSessions.id
            }
        }

    /**
     * Complete a focus session.
     */
    suspend fun completeSession(sessionId: Long, endedAt: Instant, actualDurationSeconds: Int) =
        withContext(Dispatchers.IO) {
            transaction(database) {
                FocusSessions.update({ FocusSessions.id eq sessionId }) {
                    it[FocusSessions.endedAt] = endedAt.toString()
                    it[FocusSessions.actualDurationSeconds] = actualDurationSeconds
                }
            }
        }

    private fun ResultRow.toTask(): Task {
        val taskTag = try {
            this[Tasks.tag]
        } catch (_: Exception) {
            "Deep Work"
        }
        return Task(
            id = this[Tasks.id],
            title = this[Tasks.title],
            durationMinutes = this[Tasks.durationMinutes],
            priority = Priority.valueOf(this[Tasks.priority]),
            status = TaskStatus.valueOf(this[Tasks.status]),
            createdAt = Instant.parse(this[Tasks.createdAt]),
            completedAt = this[Tasks.completedAt]?.let { Instant.parse(it) },
            tag = taskTag
        )
    }
}

