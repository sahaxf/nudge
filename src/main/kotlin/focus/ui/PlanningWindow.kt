package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
        title = "Nudge",
        icon = painterResource("icon.png"),
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
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Enter &&
                    event.isCtrlPressed && hasActiveTasks
                ) {
                    onStartFocus()
                    true
                } else {
                    false
                }
            },
        containerColor = FocusColors.DarkerBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main content column: Input + Divider + TaskList filling the area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            ) {
                // Task input section
                TaskInput(
                    onAddTask = onAddTask,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                HorizontalDivider(
                    color = FocusColors.GlassBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Task list extends full height, tasks remain visible around the floating button
                TaskList(
                    tasks = tasks,
                    selectedTaskId = selectedTask?.id,
                    onSelectTask = onSelectTask,
                    onPlayTask = onPlayTask,
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Floating "Start Focus" button
            FloatingFocusButton(
                onClick = onStartFocus,
                enabled = hasActiveTasks,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

/**
 * Floating pill button for "Start Focus", hovering over the task list.
 */
@Composable
private fun FloatingFocusButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = FocusColors.Yellow,
            contentColor = Color.Black,
            disabledContainerColor = FocusColors.Yellow.copy(alpha = 0.25f),
            disabledContentColor = Color.Black.copy(alpha = 0.35f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 12.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = FocusColors.Yellow.copy(alpha = 0.45f)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (enabled) Color.Black else Color.Black.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
            )
            Text(
                "Start Focus",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                "↵",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = if (enabled) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.3f)
                )
            )
        }
    }
}

