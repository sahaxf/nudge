package focus.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import focus.domain.Priority
import focus.domain.Task
import focus.domain.TaskStatus

/**
 * A single task row matching the UI mock:
 *
 *  ○ Fix authentication bug       15m   [HIGH]   ▶
 *
 * - Circle indicator (empty for TODO, circular progress for IN_PROGRESS, green check for COMPLETED)
 * - Task title
 * - Duration badge
 * - Priority badge (colored)
 * - Play button to start this specific task
 */
@Composable
fun TaskRow(
    task: Task,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onPlay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rowBackground = if (isSelected) {
        FocusColors.DarkSurface
    } else {
        Color.Transparent
    }

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(8.dp),
        color = rowBackground,
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.status == TaskStatus.COMPLETED) {
                        FocusColors.TextMuted
                    } else {
                        Color.White
                    }
                )
            },
            leadingContent = {
                TaskStatusIndicator(task.status)
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration
                    Text(
                        text = "${task.durationMinutes}m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FocusColors.TextMuted
                    )

                    // Play button or completion check
                    if (task.status == TaskStatus.COMPLETED) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = FocusColors.Green,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        IconButton(
                            onClick = onPlay,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start task",
                                tint = FocusColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

/**
 * Circle indicator for task status.
 */
@Composable
fun TaskStatusIndicator(status: TaskStatus) {
    val size = 16.dp
    when (status) {
        TaskStatus.COMPLETED -> {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = FocusColors.Green,
                modifier = Modifier.size(size)
            )
        }

        TaskStatus.IN_PROGRESS -> {
            Icon(
                imageVector = Icons.Default.RadioButtonUnchecked,
                contentDescription = "To do",
                tint = FocusColors.Yellow,
                modifier = Modifier.size(size)
            )
        }

        else -> {
            // Empty circle for TODO / SKIPPED
            Icon(
                imageVector = Icons.Default.RadioButtonUnchecked,
                contentDescription = "To do",
                tint = FocusColors.TextMuted,
                modifier = Modifier.size(size)
            )
        }
    }
}

/**
 * Colored priority badge matching the mock: HIGH (red), MEDIUM (yellow), LOW (muted).
 */
@Composable
fun PriorityBadge(priority: Priority, status: TaskStatus = TaskStatus.TODO) {
    if (status == TaskStatus.COMPLETED) {
        // Show "DONE" badge for completed tasks
        Badge(
            containerColor = FocusColors.Green.copy(alpha = 0.15f),
            contentColor = FocusColors.Green,
            modifier = Modifier.border(1.dp, FocusColors.Green.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
        ) {
            Text(
                text = "DONE",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
        return
    }

    val (bgColor, borderColor, textColor) = when (priority) {
        Priority.HIGH -> Triple(
            FocusColors.HighPriority.copy(alpha = 0.15f),
            FocusColors.HighPriority.copy(alpha = 0.5f),
            FocusColors.HighPriority
        )

        Priority.MEDIUM -> Triple(
            FocusColors.MediumPriority.copy(alpha = 0.15f),
            FocusColors.MediumPriority.copy(alpha = 0.5f),
            FocusColors.MediumPriority
        )

        Priority.LOW -> Triple(
            FocusColors.LowPriority.copy(alpha = 0.1f),
            FocusColors.LowPriority.copy(alpha = 0.3f),
            FocusColors.LowPriority
        )
    }

    Badge(
        containerColor = bgColor,
        contentColor = textColor,
        modifier = Modifier.border(1.dp, borderColor, RoundedCornerShape(4.dp))
    ) {
        Text(
            text = getPriorityName(priority),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

fun getPriorityName(priority: Priority): String {
    return when (priority) {
        Priority.HIGH -> "H"
        Priority.MEDIUM -> "M"
        Priority.LOW -> "L"
    }
}