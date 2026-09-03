package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * - Circle indicator (empty for TODO, filled yellow for IN_PROGRESS, green check for COMPLETED)
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Status circle indicator
            TaskStatusIndicator(task.status)

            Spacer(modifier = Modifier.width(12.dp))

            // Task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.status == TaskStatus.COMPLETED) {
                    FocusColors.TextMuted
                } else {
                    Color.White
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Duration
        Text(
            text = "${task.durationMinutes}m",
            style = MaterialTheme.typography.bodyMedium,
            color = FocusColors.TextMuted
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Priority badge
        PriorityBadge(task.priority, task.status)

        Spacer(modifier = Modifier.width(12.dp))

        // Play button or completion check
        if (task.status == TaskStatus.COMPLETED) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.titleMedium,
                color = FocusColors.Green
            )
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onPlay() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusColors.TextMuted
                )
            }
        }
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
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(FocusColors.Green),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
        TaskStatus.IN_PROGRESS -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(1.5.dp, FocusColors.Yellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {}
        }
        else -> {
            // Empty circle for TODO / SKIPPED
            Box(
                modifier = Modifier
                    .size(size)
                    .border(1.5.dp, FocusColors.TextMuted, CircleShape)
            ) {}
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
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(FocusColors.Green.copy(alpha = 0.15f))
                .border(1.dp, FocusColors.Green.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "DONE",
                style = MaterialTheme.typography.labelSmall,
                color = FocusColors.Green
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

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = getPriorityName(priority),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
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