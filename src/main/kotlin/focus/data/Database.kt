package focus.data

import org.jetbrains.exposed.sql.Table

/**
 * Exposed table definition for tasks.
 */
object Tasks : Table("tasks") {
    val id = long("id").autoIncrement()
    val title = text("title")
    val durationMinutes = integer("duration_minutes")
    val priority = text("priority")
    val status = text("status")
    val createdAt = text("created_at")
    val completedAt = text("completed_at").nullable()
    val tag = text("tag").default("Deep Work")

    override val primaryKey = PrimaryKey(id)
}

/**
 * Exposed table definition for focus sessions.
 */
object FocusSessions : Table("focus_sessions") {
    val id = long("id").autoIncrement()
    val taskId = long("task_id").references(Tasks.id)
    val startedAt = text("started_at")
    val endedAt = text("ended_at").nullable()
    val plannedDurationSeconds = integer("planned_duration_seconds")
    val actualDurationSeconds = integer("actual_duration_seconds").nullable()

    override val primaryKey = PrimaryKey(id)
}

