package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.domain.Task
import focus.domain.TaskStatus

/**
 * Modern Task List component matching the momentum design:
 * - "Up next" featured card fixed at the top with amber outline and yellow Start button
 * - "Later" section with full-width subtle line divider and task count
 * - Only later tasks are scrollable in a LazyColumn
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    selectedTaskId: Long? = null,
    onSelectTask: (Task) -> Unit = {},
    onToggleCompleted: (Task) -> Unit = {},
    onPlayTask: (Task) -> Unit = {},
    onDeleteTask: (Long) -> Unit = {},
    onStartFocus: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF141416), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF232329), RoundedCornerShape(14.dp))
                .padding(horizontal = 24.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MomentumIcons.TaskEmptyIllustration(
                    size = 120.dp,
                    slateColor = Color(0xFF525666),
                    starColor = FocusColors.MomentumYellow
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "No tasks yet",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.2).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Add a task above to get started.",
                    color = Color(0xFF8E8E98),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        return
    }

    // Determine "Up next" task and "Later" tasks
    val activeTasks = tasks.filter { it.status != TaskStatus.COMPLETED }
    val upNextTask = if (selectedTaskId != null) {
        tasks.firstOrNull { it.id == selectedTaskId && it.status != TaskStatus.COMPLETED }
            ?: activeTasks.firstOrNull()
            ?: tasks.firstOrNull()
    } else {
        activeTasks.firstOrNull() ?: tasks.firstOrNull()
    }

    val laterTasks = if (upNextTask != null) {
        tasks.filter { it.id != upNextTask.id }
    } else {
        emptyList()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 1. Fixed "Up next" Section
        if (upNextTask != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Up next",
                    color = Color(0xFFEEEEF0),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(10.dp))

                UpNextTaskCard(
                    task = upNextTask,
                    onToggleCompleted = { onToggleCompleted(upNextTask) },
                    onPlay = { onPlayTask(upNextTask) },
                    onDelete = { onDeleteTask(upNextTask.id) }
                )
            }
        }

        // 2. "Later" Section
        if (laterTasks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Fixed "Later" Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Later",
                    color = Color(0xFF8E8E98),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color(0xFF1E1E24))
                )

                Spacer(modifier = Modifier.width(14.dp))

                val countText = if (laterTasks.size == 1) "1 task" else "${laterTasks.size} tasks"
                Text(
                    text = countText,
                    color = Color(0xFF8E8E98),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Only Later tasks scroll
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(laterTasks, key = { _, task -> task.id }) { _, task ->
                    TaskRow(
                        task = task,
                        isSelected = task.id == selectedTaskId,
                        onSelect = { onSelectTask(task) },
                        onToggleCompleted = { onToggleCompleted(task) },
                        onPlay = { onPlayTask(task) },
                        onDelete = { onDeleteTask(task.id) }
                    )

                    HorizontalDivider(
                        color = Color(0xFF1E1E24),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}
