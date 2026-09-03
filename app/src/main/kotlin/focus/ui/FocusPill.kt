package focus.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.domain.TimerState

@OptIn(ExperimentalComposeUiApi::class)
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
        val infiniteTransition = rememberInfiniteTransition(label = "pause")
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
    // Hover state for subtle stop action
    // ---------------------------------------------------------

    var isStopHovered by remember { mutableStateOf(false) }
    val stopAlpha by animateFloatAsState(
        targetValue = if (isStopHovered) 0.75f else 0f,
        animationSpec = tween(150),
        label = "stopAlpha"
    )

    // ---------------------------------------------------------
    // Dimensions
    // ---------------------------------------------------------

    val pillWidth = 216.dp
    val pillHeight = 44.dp

    Box(
        modifier = modifier.size(width = 240.dp, height = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(width = 240.dp, height = 60.dp)
        ) {
            val pillWidthPx = pillWidth.toPx()
            val pillHeightPx = pillHeight.toPx()
            val radius = pillHeightPx / 2f

            val pillLeft = (size.width - pillWidthPx) / 2f
            val pillTop = (size.height - pillHeightPx) / 2f

            // =====================================================
            // 1. AMBIENT GLOW (Warm bloom around active progress)
            // =====================================================

            // if (animatedProgress > 0.1f) {
            //     val fillWidth = (pillWidthPx * animatedProgress) / 2
            //     val glowWidth = fillWidth.coerceAtLeast(radius)

            //     drawRoundRect(
            //         brush = Brush.radialGradient(
            //             colors = listOf(
            //                 Color(0x50FFA000),
            //                 Color(0x28FF8F00),
            //                 Color(0x0AE65100),
            //                 Color.Transparent
            //             ),
            //             center = Offset(
            //                 x = pillLeft + glowWidth * 0.5f,
            //                 y = pillTop + pillHeightPx * 0.75f
            //             ),
            //             radius = (glowWidth * 0.65f).coerceAtLeast(pillHeightPx * 1.5f)
            //         ),
            //         topLeft = Offset(pillLeft - 10.dp.toPx(), pillTop - 4.dp.toPx()),
            //         size = Size(glowWidth + 20.dp.toPx(), pillHeightPx + 16.dp.toPx()),
            //         cornerRadius = CornerRadius(radius + 8.dp.toPx(), radius + 8.dp.toPx())
            //     )
            // }

            // =====================================================
            // 2. PILL BASE PATH
            // =====================================================

            val pillRect = Rect(pillLeft, pillTop, pillLeft + pillWidthPx, pillTop + pillHeightPx)
            val pillPath = Path().apply {
                addRoundRect(RoundRect(pillRect, CornerRadius(radius, radius)))
            }

            // =====================================================
            // 3. DARK GLASS BASE
            // =====================================================

            drawPath(
                path = pillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x3D252932),
                        Color(0x4D12141A)
                    ),
                    startY = pillTop,
                    endY = pillTop + pillHeightPx
                )
            )

            // =====================================================
            // 4. PROGRESS FILL (Glowing Amber Capsule)
            // =====================================================

            val fillWidth = pillWidthPx * animatedProgress
            if (fillWidth > 0.01f) {
                clipPath(pillPath) {
                    // Right edge is slightly rounded consistently from beginning to end
                    val progressCornerRadius = 25.dp.toPx()
                    val fillPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(
                                    left = pillLeft,
                                    top = pillTop,
                                    right = pillLeft + fillWidth,
                                    bottom = pillTop + pillHeightPx
                                ),
                                topRight = CornerRadius(progressCornerRadius, progressCornerRadius),
                                bottomRight = CornerRadius(progressCornerRadius, progressCornerRadius)
                            )
                        )
                    }

                    // Rich amber-gold multi-stop vertical gradient
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFD54F), // Luminous golden top
                                Color(0xFFFFA000), // Rich amber body
                                Color(0xFFF57C00), // Warm amber-orange
                                Color(0xFFE65100)  // Deep glowing amber bottom
                            ),
                            startY = pillTop,
                            endY = pillTop + pillHeightPx
                        )
                    )

                    // Subtle horizontal highlight leading towards the front cap
                    drawPath(
                        path = fillPath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0x15FFD54F),
                                Color(0x00FFB300),
                                Color(0x28FFE082)
                            ),
                            startX = pillLeft,
                            endX = pillLeft + fillWidth
                        )
                    )

                    // Liquid glass specular highlight along the upper half of progress
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.32f),
                                Color.White.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            startY = pillTop,
                            endY = pillTop + pillHeightPx * 0.48f
                        )
                    )
                }
            }

            // =====================================================
            // 5. GLASS SPECULAR SHEEN (Overall pill reflection)
            // =====================================================

            clipPath(pillPath) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.09f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        ),
                        startY = pillTop,
                        endY = pillTop + pillHeightPx * 0.45f
                    ),
                    topLeft = Offset(pillLeft, pillTop),
                    size = Size(pillWidthPx, pillHeightPx * 0.45f)
                )
            }

            // =====================================================
            // 6. GLASS BORDER / SPECULAR RIM
            // =====================================================

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.12f)
                    ),
                    startY = pillTop,
                    endY = pillTop + pillHeightPx
                ),
                topLeft = Offset(pillLeft, pillTop),
                size = Size(pillWidthPx, pillHeightPx),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = 1.2.dp.toPx())
            )

            // =====================================================
            // 7. CENTERED TIMER TEXT
            // =====================================================

            val textStyle = TextStyle(
                color = Color.White.copy(alpha = pauseAlpha),
                fontSize = if (timerState is TimerState.Completed) 22.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.6.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.38f),
                    offset = Offset(0f, 1f),
                    blurRadius = 3f
                )
            )

            val textLayout = textMeasurer.measure(
                text = displayText,
                style = textStyle
            )

            // Perfectly centered horizontally and vertically in the pill
            drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
                    x = pillLeft + (pillWidthPx - textLayout.size.width) / 2f,
                    y = pillTop + (pillHeightPx - textLayout.size.height) / 2f
                )
            )

            // =====================================================
            // 8. SUBTLE STOP ICON (Revealed smoothly on hover)
            // =====================================================

            if (stopAlpha > 0.01f) {
                val stopSize = 9.dp.toPx()
                val stopCenter = Offset(
                    x = pillLeft + pillWidthPx - radius * 0.9f,
                    y = pillTop + pillHeightPx / 2f
                )

                drawRoundRect(
                    color = Color.White.copy(alpha = stopAlpha),
                    topLeft = Offset(
                        x = stopCenter.x - stopSize / 2f,
                        y = stopCenter.y - stopSize / 2f
                    ),
                    size = Size(stopSize, stopSize),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }

        // =========================================================
        // STOP BUTTON HIT TARGET (Right cap of pill)
        // =========================================================

        Box(
            modifier = Modifier
                .size(width = 240.dp, height = 60.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width = 44.dp, height = 44.dp)
                    .onPointerEvent(PointerEventType.Enter) { isStopHovered = true }
                    .onPointerEvent(PointerEventType.Exit) { isStopHovered = false }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onStop
                    )
            )
        }
    }
}