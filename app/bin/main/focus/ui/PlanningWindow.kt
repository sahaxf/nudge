package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import focus.domain.Task
import focus.domain.TaskStatus
import focus.state.AppState

/**
 * The planning window — the main application window.
 * Matches the UI mock: title "Focus", task input, task list, Start Focus button.
 */
@Composable
fun PlanningWindow(
    appState: AppState,
    onCloseRequest: () -> Unit
) {
    val tasks by appState.tasks.collectAsState()
    val selectedTask by appState.selectedTask.collectAsState()

    val windowState = rememberWindowState(
        size = DpSize(600.dp, 640.dp),
        position = WindowPosition.PlatformDefault
    )

    Window(
        onCloseRequest = onCloseRequest,
        title = "Focus",
        state = windowState,
        resizable = true
    ) {
        FocusTheme {
            PlanningContent(
                tasks = tasks,
                selectedTask = selectedTask,
                onAddTask = { title, duration, priority ->
                    appState.addTask(title, duration, priority)
                },
                onSelectTask = { appState.selectTask(it) },
                onPlayTask = { task ->
                    appState.selectTask(task)
                    appState.startFocus()
                },
                onStartFocus = { appState.startFocus() },
                onDeleteTask = { appState.deleteTask(it) },
                hasActiveTasks = tasks.any { it.status == TaskStatus.TODO }
            )
        }
    }
}

/**
 * Planning window content (separated for testability).
 */
@Composable
fun PlanningContent(
    tasks: List<Task>,
    selectedTask: Task?,
    onAddTask: (String, Int, focus.domain.Priority) -> Unit,
    onSelectTask: (Task) -> Unit,
    onPlayTask: (Task) -> Unit,
    onStartFocus: () -> Unit,
    onDeleteTask: (Long) -> Unit,
    hasActiveTasks: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FocusColors.DarkerBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Enter &&
                        event.isCtrlPressed && hasActiveTasks
                    ) {
                        onStartFocus()
                        true
                    } else {
                        false
                    }
                }
        ) {
            // Task input section
            TaskInput(
                onAddTask = onAddTask,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            HorizontalDivider(
                color = FocusColors.GlassBorder,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Task list
            TaskList(
                tasks = tasks,
                selectedTaskId = selectedTask?.id,
                onSelectTask = onSelectTask,
                onPlayTask = onPlayTask,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Start Focus button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onStartFocus,
                    enabled = hasActiveTasks,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusColors.Yellow,
                        contentColor = Color.Black,
                        disabledContainerColor = FocusColors.Yellow.copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 14.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        "Start Focus",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Enter ↵",
                    style = MaterialTheme.typography.bodySmall,
                    color = FocusColors.TextMuted
                )
            }
        }
    }
}

