package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import focus.domain.TimerState
import focus.platform.WindowManager
import focus.state.AppState

@Composable
fun FocusWindow(
    appState: AppState
) {
    val timerState by appState.timer.state.collectAsState()

    val position = remember {
        WindowManager.getFocusPillPosition()
    }

    val windowState = rememberWindowState(
        size = DpSize(
            width = 250.dp,
            height = 64.dp
        ),
        position = WindowPosition(
            position.x.dp,
            position.y.dp
        )
    )

    Window(
        onCloseRequest = {
            appState.stopSession()
        },
        title = "Focus Timer",
        icon = painterResource("icon.png"),
        state = windowState,
        undecorated = true,
        transparent = true,
        resizable = false,
        alwaysOnTop = true,
        focusable = true
    ) {
        FocusTheme {

            WindowDraggableArea(
                modifier = Modifier.fillMaxSize()
            ) {
                FocusPill(
                    timerState = timerState,
                    onStop = {
                        appState.stopSession()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .onPreviewKeyEvent { event ->

                            if (event.type != KeyEventType.KeyDown) {
                                return@onPreviewKeyEvent false
                            }

                            when (event.key) {

                                Key.Spacebar -> {
                                    appState.togglePause()
                                    true
                                }

                                Key.Enter -> {
                                    appState.completeCurrentTask()
                                    true
                                }

                                Key.Escape -> {
                                    appState.stopSession()
                                    true
                                }

                                else -> false
                            }
                        }
                )
            }
        }
    }
}