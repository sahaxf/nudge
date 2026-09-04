package focus

import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import focus.data.TaskRepository
import focus.state.AppMode
import focus.state.AppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import focus.ui.FocusWindow
import focus.ui.PlanningWindow

/**
 * FocusHD — a minimal desktop focus timer.
 *
 * Entry point: initializes database, creates app state,
 * and launches the appropriate window based on the current mode.
 */
fun main() = application {
    val scope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    val repository = remember { TaskRepository() }
    val appState = remember { AppState(repository, scope) }

    val appMode by appState.appMode.collectAsState()

    when (appMode) {
        AppMode.PLANNING -> {
            PlanningWindow(
                appState = appState,
                onCloseRequest = ::exitApplication
            )
        }
        AppMode.FOCUS -> {
            FocusWindow(appState = appState)
        }
    }
}

