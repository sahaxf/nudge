package focus.domain

import kotlinx.datetime.Instant

/**
 * Record of a completed or in-progress focus session.
 */
data class FocusSession(
    val id: Long = 0,
    val taskId: Long,
    val startedAt: Instant,
    val endedAt: Instant? = null,
    val plannedDurationSeconds: Int,
    val actualDurationSeconds: Int? = null
)

