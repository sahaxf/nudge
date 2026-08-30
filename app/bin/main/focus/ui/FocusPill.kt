package focus.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.domain.TimerState


@Composable
fun FocusPill(
    timerState: TimerState,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // ---------------------------------------------------------
    // Progress
    // ---------------------------------------------------------

    val targetProgress = when (timerState) {
        is TimerState.Running -> timerState.progress
        is TimerState.Paused -> timerState.progress
        is TimerState.Completed -> 1f
        is TimerState.Idle -> 0f
    }.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = "progress"
    )

    // ---------------------------------------------------------
    // Completion animation
    // ---------------------------------------------------------

    val completionScale = remember {
        Animatable(1f)
    }

    LaunchedEffect(timerState) {
        if (timerState is TimerState.Completed) {
            completionScale.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(200)
            )

            completionScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(200)
            )
        }
    }

    // ---------------------------------------------------------
    // Display text
    // ---------------------------------------------------------

    val displayText = when (timerState) {
        is TimerState.Running -> timerState.displayTime
        is TimerState.Paused -> timerState.displayTime
        is TimerState.Completed -> "✓"
        is TimerState.Idle -> "--:--"
    }

    // ---------------------------------------------------------
    // Paused animation
    // ---------------------------------------------------------

    val pauseAlpha = if (timerState is TimerState.Paused) {
        val infiniteTransition = rememberInfiniteTransition(
            label = "pause"
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pauseAlpha"
        )

        alpha
    } else {
        1f
    }

    // ---------------------------------------------------------
    // Dimensions
    // ---------------------------------------------------------

    val pillWidth = 200.dp
    val pillHeight = 56.dp
    val cornerRadius = 28.dp

    Box(
        modifier = modifier.size(
            width = pillWidth,
            height = pillHeight
        ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = modifier
                .size(
                    width = pillWidth,
                    height = pillHeight
                ),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                val radius = cornerRadius.toPx()

                val pillPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(
                                0f,
                                0f,
                                size.width,
                                size.height
                            ),
                            cornerRadius = CornerRadius(
                                radius,
                                radius
                            )
                        )
                    )
                }

                // =====================================================
                // GLASS BASE
                // =====================================================

                drawPath(
                    path = pillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            FocusColors.GlassTop,
                            FocusColors.GlassBottom
                        )
                    )
                )

                // =====================================================
                // PROGRESS
                // =====================================================

                val fillWidth = size.width * animatedProgress

                if (fillWidth > 0f) {
                    clipPath(pillPath) {

                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    FocusColors.ProgressStart,
                                    FocusColors.ProgressEnd
                                )
                            ),
                            topLeft = Offset.Zero,
                            size = Size(
                                width = fillWidth,
                                height = size.height
                            )
                        )

                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    FocusColors.ProgressHighlight,
                                    Color.Transparent
                                )
                            ),
                            topLeft = Offset.Zero,
                            size = Size(
                                width = fillWidth,
                                height = size.height
                            )
                        )
                    }
                }

                // =====================================================
                // GLASS HIGHLIGHT
                // =====================================================

                clipPath(pillPath) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.075f),
                                Color.Transparent
                            )
                        ),
                        topLeft = Offset.Zero,
                        size = Size(
                            width = size.width,
                            height = size.height * 0.55f
                        )
                    )
                }

                // =====================================================
                // BORDER
                // =====================================================

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            FocusColors.BorderHighlight,
                            FocusColors.Border
                        )
                    ),
                    cornerRadius = CornerRadius(
                        radius,
                        radius
                    ),
                    style = Stroke(
                        width = 1.dp.toPx()
                    )
                )

                // =====================================================
                // TIMER
                // =====================================================

                val textStyle = TextStyle(
                    color = Color.White.copy(alpha = pauseAlpha),
                    fontSize = if (
                        timerState is TimerState.Completed
                    ) {
                        24.sp
                    } else {
                        20.sp
                    },
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.5.sp
                )

                val textLayout = textMeasurer.measure(
                    text = displayText,
                    style = textStyle
                )

                // Center timer in the WHOLE pill.
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x = (size.width - textLayout.size.width) / 2f,
                        y = (size.height - textLayout.size.height) / 2f
                    )
                )
            }

            // =========================================================
            // STOP BUTTON
            // =========================================================
            //
            // Integrated into the pill. It does not have its own
            // background — it is simply a clickable glass area.
            //

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(52.dp)
                    .clickable(
                        onClick = onStop
                    ),
                contentAlignment = Alignment.Center
            ) {

                Canvas(
                    modifier = Modifier.size(48.dp)
                ) {

                    val stopSize = 12.dp.toPx()

                    // button element
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.82f),
                        topLeft = Offset(
                            x = (size.width - stopSize) / 2f,
                            y = (size.height - stopSize) / 2f
                        ),
                        size = Size(
                            width = stopSize,
                            height = stopSize
                        ),
                        cornerRadius = CornerRadius(
                            2.5.dp.toPx(),
                            2.5.dp.toPx()
                        )
                    )
                }
            }
        }

        // -----------------------------------------------------
        // Clickable stop area
        // -----------------------------------------------------

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(58.dp)
                .clickable(
                    onClick = onStop
                )
        )
    }
}