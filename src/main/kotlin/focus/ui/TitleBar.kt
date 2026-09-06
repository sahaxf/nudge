package focus.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState
import java.awt.Cursor
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * Cache for the extracted transparent Momentum 3D logo bitmap.
 */
private object TitleBarLogoCache {
    private var cachedBitmap: ImageBitmap? = null
    private var failed = false

    fun getBitmap(): ImageBitmap? {
        if (cachedBitmap != null) return cachedBitmap
        if (failed) return null

        return try {
            val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("icon.png")
            if (stream == null) {
                failed = true
                return null
            }
            val original = ImageIO.read(stream)

            // Find tight bounding box for the yellow icon
            var minX = original.width
            var maxX = 0
            var minY = original.height
            var maxY = 0

            for (y in 0 until original.height) {
                for (x in 0 until original.width) {
                    val rgb = original.getRGB(x, y)
                    val r = (rgb shr 16) and 0xFF
                    val g = (rgb shr 8) and 0xFF
                    val b = rgb and 0xFF
                    val brightness = (r + g + b) / 3
                    if (brightness > 65 && r > 110 && g > 90) {
                        if (x < minX) minX = x
                        if (x > maxX) maxX = x
                        if (y < minY) minY = y
                        if (y > maxY) maxY = y
                    }
                }
            }

            if (minX >= maxX || minY >= maxY) {
                failed = true
                return null
            }

            val pad = 6
            minX = (minX - pad).coerceAtLeast(0)
            maxX = (maxX + pad).coerceAtMost(original.width - 1)
            minY = (minY - pad).coerceAtLeast(0)
            maxY = (maxY + pad).coerceAtMost(original.height - 1)

            val w = maxX - minX + 1
            val h = maxY - minY + 1
            val resultImg = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

            for (cy in 0 until h) {
                for (cx in 0 until w) {
                    val ox = minX + cx
                    val oy = minY + cy
                    val rgb = original.getRGB(ox, oy)
                    val a = (rgb shr 24) and 0xFF
                    val r = (rgb shr 16) and 0xFF
                    val g = (rgb shr 8) and 0xFF
                    val b = rgb and 0xFF
                    val brightness = (r + g + b) / 3

                    if (a == 0 || brightness < 50 || (r < 75 && g < 75 && b < 75)) {
                        resultImg.setRGB(cx, cy, 0)
                    } else {
                        val alphaFactor = ((brightness - 50).toFloat() / 25f).coerceIn(0f, 1f)
                        val finalAlpha = (a * alphaFactor).toInt()
                        val newRgb = (finalAlpha shl 24) or (r shl 16) or (g shl 8) or b
                        resultImg.setRGB(cx, cy, newRgb)
                    }
                }
            }

            val composeBitmap = resultImg.toComposeImageBitmap()
            cachedBitmap = composeBitmap
            composeBitmap
        } catch (e: Exception) {
            failed = true
            null
        }
    }
}

/**
 * Renders the Momentum logo glyph in the title bar.
 */
@Composable
fun TitleBarLogo(modifier: Modifier = Modifier) {
    val bitmap = remember { TitleBarLogoCache.getBitmap() }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Momentum Logo",
            modifier = modifier.size(width = 16.dp, height = 14.dp)
        )
    } else {
        // Fallback vector drawing matching the yellow zigzag brand mark
        Canvas(modifier = modifier.size(width = 16.dp, height = 14.dp)) {
            val w = size.width
            val h = size.height
            val yellow = FocusColors.MomentumYellow

            // Speed lines on left
            drawLine(
                color = yellow,
                start = Offset(w * 0.05f, h * 0.40f),
                end = Offset(w * 0.22f, h * 0.40f),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = yellow,
                start = Offset(w * 0.00f, h * 0.58f),
                end = Offset(w * 0.38f, h * 0.58f),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = yellow,
                start = Offset(w * 0.05f, h * 0.76f),
                end = Offset(w * 0.26f, h * 0.76f),
                strokeWidth = 1.8.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Slanted ribbon bars
            drawLine(
                color = yellow,
                start = Offset(w * 0.35f, h * 0.82f),
                end = Offset(w * 0.58f, h * 0.18f),
                strokeWidth = 3.2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = yellow,
                start = Offset(w * 0.58f, h * 0.82f),
                end = Offset(w * 0.82f, h * 0.18f),
                strokeWidth = 3.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Custom modern desktop title bar matching the Momentum design.
 */
@Composable
fun WindowScope.TitleBar(
    windowState: WindowState,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMaximized = windowState.placement == WindowPlacement.Maximized

    WindowDraggableArea(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(FocusColors.AppBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo + Title
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleBarLogo()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "momentum",
                    color = Color(0xFFCCCCCC),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = GoogleSansFontFamily,
                    letterSpacing = (-0.2).sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Window controls (Minimize, Maximize / Restore, Close)
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Minimize
                TitleBarButton(
                    onClick = { windowState.isMinimized = true },
                    contentDescription = "Minimize"
                ) { color ->
                    Canvas(modifier = Modifier.size(10.dp)) {
                        val w = size.width
                        val h = size.height
                        drawLine(
                            color = color,
                            start = Offset(0f, h * 0.55f),
                            end = Offset(w, h * 0.55f),
                            strokeWidth = 1.3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Maximize / Restore
                TitleBarButton(
                    onClick = {
                        windowState.placement = if (isMaximized) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Maximized
                        }
                    },
                    contentDescription = if (isMaximized) "Restore" else "Maximize"
                ) { color ->
                    Canvas(modifier = Modifier.size(10.dp)) {
                        val w = size.width
                        val h = size.height
                        if (isMaximized) {
                            val stroke = 1.1.dp.toPx()
                            val boxSize = w * 0.70f
                            // Back box (upper right)
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(w * 0.30f, 0f),
                                size = Size(boxSize, boxSize),
                                cornerRadius = CornerRadius(1.dp.toPx()),
                                style = Stroke(width = stroke)
                            )
                            // Front box (lower left)
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(0f, h * 0.30f),
                                size = Size(boxSize, boxSize),
                                cornerRadius = CornerRadius(1.dp.toPx()),
                                style = Stroke(width = stroke)
                            )
                        } else {
                            // Maximize icon: single square
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(0.5f, 0.5f),
                                size = Size(w - 1f, h - 1f),
                                cornerRadius = CornerRadius(1.5.dp.toPx()),
                                style = Stroke(width = 1.3.dp.toPx())
                            )
                        }
                    }
                }

                // Close
                TitleBarButton(
                    onClick = onCloseRequest,
                    isClose = true,
                    contentDescription = "Close"
                ) { color ->
                    Canvas(modifier = Modifier.size(10.dp)) {
                        val w = size.width
                        val h = size.height
                        val stroke = 1.3.dp.toPx()
                        drawLine(
                            color = color,
                            start = Offset(0.5f, 0.5f),
                            end = Offset(w - 0.5f, h - 0.5f),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = color,
                            start = Offset(w - 0.5f, 0.5f),
                            end = Offset(0.5f, h - 0.5f),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}

/**
 * Sleek window control button with hover feedback.
 */
@Composable
private fun TitleBarButton(
    onClick: () -> Unit,
    isClose: Boolean = false,
    contentDescription: String,
    icon: @Composable (Color) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val normalIconColor = Color(0xFF9E9EA4)
    val hoveredIconColor = Color.White
    val normalBg = Color.Transparent
    val hoveredBg = if (isClose) Color(0xFFE81123) else Color.White.copy(alpha = 0.08f)

    val bgColor by animateColorAsState(
        targetValue = if (isHovered) hoveredBg else normalBg,
        animationSpec = tween(durationMillis = 120)
    )
    val iconColor by animateColorAsState(
        targetValue = if (isHovered) hoveredIconColor else normalIconColor,
        animationSpec = tween(durationMillis = 120)
    )

    Box(
        modifier = Modifier
            .size(width = 34.dp, height = 26.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        icon(iconColor)
    }
}

/**
 * Provides smooth drag-resizing around the borders of undecorated windows.
 */
@Composable
fun WindowResizeOverlay(
    window: ComposeWindow,
    windowState: WindowState,
    minWidth: Int = 680,
    minHeight: Int = 500,
    borderThickness: Int = 6
) {
    if (windowState.placement == WindowPlacement.Maximized) return

    val thickness = borderThickness.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // Right edge
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(thickness)
                .align(Alignment.CenterEnd)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newWidth = (window.width + dragAmount.x.toInt()).coerceAtLeast(minWidth)
                        window.setSize(newWidth, window.height)
                    }
                }
        )

        // Left edge
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(thickness)
                .align(Alignment.CenterStart)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newWidth = (window.width - dragAmount.x.toInt()).coerceAtLeast(minWidth)
                        val dx = window.width - newWidth
                        window.setBounds(window.x + dx, window.y, newWidth, window.height)
                    }
                }
        )

        // Bottom edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thickness)
                .align(Alignment.BottomCenter)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newHeight = (window.height + dragAmount.y.toInt()).coerceAtLeast(minHeight)
                        window.setSize(window.width, newHeight)
                    }
                }
        )

        // Bottom-Right corner
        Box(
            modifier = Modifier
                .size(thickness * 2)
                .align(Alignment.BottomEnd)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newWidth = (window.width + dragAmount.x.toInt()).coerceAtLeast(minWidth)
                        val newHeight = (window.height + dragAmount.y.toInt()).coerceAtLeast(minHeight)
                        window.setSize(newWidth, newHeight)
                    }
                }
        )

        // Bottom-Left corner
        Box(
            modifier = Modifier
                .size(thickness * 2)
                .align(Alignment.BottomStart)
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val newWidth = (window.width - dragAmount.x.toInt()).coerceAtLeast(minWidth)
                        val dx = window.width - newWidth
                        val newHeight = (window.height + dragAmount.y.toInt()).coerceAtLeast(minHeight)
                        window.setBounds(window.x + dx, window.y, newWidth, newHeight)
                    }
                }
        )
    }
}
