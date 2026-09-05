package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import focus.domain.Priority
import focus.domain.Task
import focus.domain.TaskStatus
import focus.state.AppState

/**
 * The planning window — Momentum desktop interface.
 * Implements the 2-column dashboard layout with sidebar navigation,
 * quick task input, styled task list, and full-width "Start focusing" action button.
 */
@Composable
fun PlanningWindow(
    appState: AppState,
    onCloseRequest: () -> Unit
) {
    val tasks by appState.tasks.collectAsState()
    val selectedTask by appState.selectedTask.collectAsState()

    val windowState = rememberWindowState(
        size = DpSize(900.dp, 720.dp),
        position = WindowPosition.PlatformDefault
    )

    Window(
        onCloseRequest = onCloseRequest,
        title = "momentum",
        icon = painterResource("icon.png"),
        state = windowState,
        resizable = true
    ) {
        FocusTheme {
            PlanningContent(
                tasks = tasks,
                selectedTask = selectedTask,
                onAddTask = { title, duration, priority, tag ->
                    appState.addTask(title, duration, priority, tag)
                },
                onSelectTask = { appState.selectTask(it) },
                onToggleCompleted = { appState.toggleTask(it) },
                onPlayTask = { task ->
                    appState.selectTask(task)
                    appState.startFocus()
                },
                onStartFocus = {
                    if (tasks.none { it.status == TaskStatus.TODO }) {
                        appState.addTask("Focus Session", 25, Priority.MEDIUM, "Deep Work")
                    }
                    appState.startFocus()
                },
                onDeleteTask = { appState.deleteTask(it) },
                hasActiveTasks = tasks.any { it.status == TaskStatus.TODO }
            )
        }
    }
}

/**
 * Planning window content matching the Momentum mock.
 */
@Composable
fun PlanningContent(
    tasks: List<Task>,
    selectedTask: Task?,
    onAddTask: (String, Int, Priority, String) -> Unit,
    onSelectTask: (Task) -> Unit,
    onToggleCompleted: (Task) -> Unit,
    onPlayTask: (Task) -> Unit,
    onStartFocus: () -> Unit,
    onDeleteTask: (Long) -> Unit,
    hasActiveTasks: Boolean
) {
    var currentRoute by remember { mutableStateOf("Dashboard") }

    Surface(
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
        color = FocusColors.AppBackground
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. Left Sidebar
            Sidebar(
                currentRoute = currentRoute,
                onNavigate = { currentRoute = it }
            )

            // Vertical divider between sidebar and main content
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(FocusColors.DividerColor)
            )

            // 2. Right Main Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 36.dp, vertical = 28.dp)
            ) {
                // Header: Greeting + Settings gear
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Good morning, Akash 👋",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )

                    // Settings gear icon button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        MomentumIcons.Settings(
                            color = FocusColors.TextSecondary,
                            size = 20.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Task Input Bar
                TaskInput(
                    onAddTask = onAddTask
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Tasks List Section with floating start focusing button
                TaskList(
                    tasks = tasks,
                    selectedTaskId = selectedTask?.id,
                    onSelectTask = onSelectTask,
                    onToggleCompleted = onToggleCompleted,
                    onPlayTask = onPlayTask,
                    onDeleteTask = onDeleteTask,
                    onStartFocus = onStartFocus,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}
