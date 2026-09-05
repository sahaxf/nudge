package focus.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom vector icons rendered via Canvas to match the exact Momentum aesthetic.
 */
object MomentumIcons {

    @Composable
    fun Logo(modifier: Modifier = Modifier, size: Dp = 34.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val r = w * 0.46f

            // Yellow background circle
            drawCircle(
                color = FocusColors.MomentumYellow,
                radius = r,
                center = Offset(w / 2f, h / 2f)
            )

            // Black wave / cursive 'w' glyph
            val strokeWidth = w * 0.11f
            val path = Path().apply {
                moveTo(w * 0.28f, h * 0.42f)
                cubicTo(
                    w * 0.28f, h * 0.65f,
                    w * 0.38f, h * 0.65f,
                    w * 0.44f, h * 0.44f
                )
                cubicTo(
                    w * 0.49f, h * 0.28f,
                    w * 0.53f, h * 0.28f,
                    w * 0.58f, h * 0.44f
                )
                cubicTo(
                    w * 0.63f, h * 0.65f,
                    w * 0.72f, h * 0.65f,
                    w * 0.73f, h * 0.44f
                )
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

    @Composable
    fun Dashboard(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 18.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.10f

            val roof = Path().apply {
                moveTo(w * 0.15f, h * 0.48f)
                lineTo(w * 0.50f, h * 0.18f)
                lineTo(w * 0.85f, h * 0.48f)
            }
            drawPath(roof, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

            val body = Path().apply {
                moveTo(w * 0.25f, h * 0.45f)
                lineTo(w * 0.25f, h * 0.82f)
                lineTo(w * 0.75f, h * 0.82f)
                lineTo(w * 0.75f, h * 0.45f)
            }
            drawPath(body, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Small door outline
            val door = Path().apply {
                moveTo(w * 0.42f, h * 0.82f)
                lineTo(w * 0.42f, h * 0.62f)
                lineTo(w * 0.58f, h * 0.62f)
                lineTo(w * 0.58f, h * 0.82f)
            }
            drawPath(door, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    @Composable
    fun Insights(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 18.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.11f

            drawLine(color = color, start = Offset(w * 0.25f, h * 0.80f), end = Offset(w * 0.25f, h * 0.52f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(color = color, start = Offset(w * 0.50f, h * 0.80f), end = Offset(w * 0.50f, h * 0.22f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(color = color, start = Offset(w * 0.75f, h * 0.80f), end = Offset(w * 0.75f, h * 0.38f), strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }

    @Composable
    fun Sessions(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 18.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.10f
            val r = w * 0.36f
            val c = Offset(w / 2f, h / 2f)

            drawCircle(color = color, radius = r, center = c, style = Stroke(width = stroke))
            drawLine(color = color, start = c, end = Offset(c.x, c.y - r * 0.60f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(color = color, start = c, end = Offset(c.x + r * 0.50f, c.y), strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }

    @Composable
    fun Tasks(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 18.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.10f

            drawRoundRect(
                color = color,
                topLeft = Offset(w * 0.18f, h * 0.18f),
                size = Size(w * 0.64f, h * 0.64f),
                cornerRadius = CornerRadius(w * 0.15f, w * 0.15f),
                style = Stroke(width = stroke)
            )

            val check = Path().apply {
                moveTo(w * 0.34f, h * 0.50f)
                lineTo(w * 0.46f, h * 0.64f)
                lineTo(w * 0.66f, h * 0.36f)
            }
            drawPath(check, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    @Composable
    fun Tags(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 18.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.10f

            val tag = Path().apply {
                moveTo(w * 0.18f, h * 0.50f)
                lineTo(w * 0.46f, h * 0.22f)
                lineTo(w * 0.80f, h * 0.22f)
                lineTo(w * 0.80f, h * 0.56f)
                lineTo(w * 0.52f, h * 0.84f)
                close()
            }
            drawPath(tag, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawCircle(color = color, radius = stroke * 0.7f, center = Offset(w * 0.66f, h * 0.36f))
        }
    }

    @Composable
    fun Settings(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 20.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.09f
            val r = w * 0.34f
            val c = Offset(w / 2f, h / 2f)

            drawCircle(color = color, radius = r, center = c, style = Stroke(width = stroke))
            drawCircle(color = color, radius = r * 0.38f, center = c, style = Stroke(width = stroke * 0.9f))

            // 6 cog teeth
            for (i in 0 until 6) {
                val angle = (i * 60.0) * (Math.PI / 180.0)
                val cos = Math.cos(angle).toFloat()
                val sin = Math.sin(angle).toFloat()
                drawLine(
                    color = color,
                    start = Offset(c.x + cos * r * 0.80f, c.y + sin * r * 0.80f),
                    end = Offset(c.x + cos * r * 1.25f, c.y + sin * r * 1.25f),
                    strokeWidth = stroke * 1.2f,
                    cap = StrokeCap.Round
                )
            }
        }
    }

    @Composable
    fun Clock(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 14.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = (w * 0.11f).coerceAtLeast(1.2f)
            val r = w * 0.40f
            val c = Offset(w / 2f, h / 2f)

            drawCircle(color = color, radius = r, center = c, style = Stroke(width = stroke))
            drawLine(color = color, start = c, end = Offset(c.x, c.y - r * 0.55f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(color = color, start = c, end = Offset(c.x + r * 0.50f, c.y), strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }

    @Composable
    fun Tag(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 14.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = (w * 0.11f).coerceAtLeast(1.2f)

            withTransform({
                rotate(-40f, pivot = Offset(w * 0.5f, h * 0.5f))
            }) {
                val tagPath = Path().apply {
                    moveTo(w * 0.16f, h * 0.50f)
                    lineTo(w * 0.38f, h * 0.22f)
                    lineTo(w * 0.84f, h * 0.22f)
                    lineTo(w * 0.84f, h * 0.78f)
                    lineTo(w * 0.38f, h * 0.78f)
                    close()
                }
                drawPath(
                    tagPath,
                    color = color,
                    style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawCircle(
                    color = color,
                    radius = stroke * 0.75f,
                    center = Offset(w * 0.68f, h * 0.50f)
                )
            }
        }
    }

    @Composable
    fun ChevronDown(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 14.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.14f

            val chevron = Path().apply {
                moveTo(w * 0.25f, h * 0.38f)
                lineTo(w * 0.50f, h * 0.65f)
                lineTo(w * 0.75f, h * 0.38f)
            }
            drawPath(chevron, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    @Composable
    fun Sort(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 14.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.12f

            drawLine(color = color, start = Offset(w * 0.15f, h * 0.28f), end = Offset(w * 0.85f, h * 0.28f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(color = color, start = Offset(w * 0.25f, h * 0.50f), end = Offset(w * 0.75f, h * 0.50f), strokeWidth = stroke, cap = StrokeCap.Round)
            drawLine(color = color, start = Offset(w * 0.35f, h * 0.72f), end = Offset(w * 0.65f, h * 0.72f), strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }

    @Composable
    fun ReturnArrow(modifier: Modifier = Modifier, color: Color = FocusColors.TextMuted, size: Dp = 18.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.11f

            val arrowPath = Path().apply {
                moveTo(w * 0.75f, h * 0.30f)
                lineTo(w * 0.75f, h * 0.65f)
                lineTo(w * 0.28f, h * 0.65f)
            }
            drawPath(arrowPath, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))

            // Arrowhead pointing left
            val head = Path().apply {
                moveTo(w * 0.42f, h * 0.50f)
                lineTo(w * 0.26f, h * 0.65f)
                lineTo(w * 0.42f, h * 0.80f)
            }
            drawPath(head, color = color, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    @Composable
    fun MoreHorizontal(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 16.dp) {
        Canvas(modifier = modifier.size(size)) {
            val r = this.size.height * 0.12f
            val cy = this.size.height / 2f
            drawCircle(color = color, radius = r, center = Offset(this.size.width * 0.22f, cy))
            drawCircle(color = color, radius = r, center = Offset(this.size.width * 0.50f, cy))
            drawCircle(color = color, radius = r, center = Offset(this.size.width * 0.78f, cy))
        }
    }

    @Composable
    fun CircleCheckbox(
        checked: Boolean,
        modifier: Modifier = Modifier,
        size: Dp = 22.dp,
        uncheckedColor: Color = Color(0xFF3F3F4A)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val r = w * 0.42f
            val c = Offset(w / 2f, h / 2f)

            if (checked) {
                drawCircle(color = FocusColors.Green, radius = r, center = c, style = Fill)
                val check = Path().apply {
                    moveTo(w * 0.30f, h * 0.50f)
                    lineTo(w * 0.44f, h * 0.66f)
                    lineTo(w * 0.70f, h * 0.36f)
                }
                drawPath(check, color = Color.Black, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            } else {
                drawCircle(color = uncheckedColor, radius = r, center = c, style = Stroke(width = (w * 0.08f).coerceAtLeast(1.5f)))
            }
        }
    }

    @Composable
    fun EditPencil(
        modifier: Modifier = Modifier,
        color: Color = FocusColors.TextSecondary,
        size: Dp = 16.dp
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = (w * 0.10f).coerceAtLeast(1.2f)

            val body = Path().apply {
                moveTo(w * 0.64f, h * 0.14f)
                lineTo(w * 0.86f, h * 0.36f)
                lineTo(w * 0.38f, h * 0.84f)
                lineTo(w * 0.14f, h * 0.86f)
                lineTo(w * 0.16f, h * 0.62f)
                close()
            }
            drawPath(
                body,
                color = color,
                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            drawLine(
                color = color,
                start = Offset(w * 0.28f, h * 0.74f),
                end = Offset(w * 0.38f, h * 0.84f),
                strokeWidth = stroke * 0.85f,
                cap = StrokeCap.Round
            )
        }
    }

    @Composable
    fun TrashCan(
        modifier: Modifier = Modifier,
        color: Color = FocusColors.TextSecondary,
        size: Dp = 16.dp
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = (w * 0.10f).coerceAtLeast(1.2f)

            // Horizontal lid line
            drawLine(
                color = color,
                start = Offset(w * 0.15f, h * 0.22f),
                end = Offset(w * 0.85f, h * 0.22f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            // Top handle loop
            val handle = Path().apply {
                moveTo(w * 0.36f, h * 0.22f)
                lineTo(w * 0.36f, h * 0.12f)
                lineTo(w * 0.64f, h * 0.12f)
                lineTo(w * 0.64f, h * 0.22f)
            }
            drawPath(
                handle,
                color = color,
                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Can body
            val body = Path().apply {
                moveTo(w * 0.25f, h * 0.22f)
                lineTo(w * 0.29f, h * 0.80f)
                quadraticTo(w * 0.30f, h * 0.88f, w * 0.38f, h * 0.88f)
                lineTo(w * 0.62f, h * 0.88f)
                quadraticTo(w * 0.70f, h * 0.88f, w * 0.71f, h * 0.80f)
                lineTo(w * 0.75f, h * 0.22f)
            }
            drawPath(
                body,
                color = color,
                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Vertical ribs inside the can
            drawLine(
                color = color,
                start = Offset(w * 0.42f, h * 0.36f),
                end = Offset(w * 0.42f, h * 0.74f),
                strokeWidth = stroke * 0.85f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(w * 0.58f, h * 0.36f),
                end = Offset(w * 0.58f, h * 0.74f),
                strokeWidth = stroke * 0.85f,
                cap = StrokeCap.Round
            )
        }
    }

    @Composable
    fun PlayTriangle(modifier: Modifier = Modifier, color: Color = Color.Black, size: Dp = 16.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val triangle = Path().apply {
                moveTo(w * 0.28f, h * 0.20f)
                lineTo(w * 0.82f, h * 0.50f)
                lineTo(w * 0.28f, h * 0.80f)
                close()
            }
            drawPath(triangle, color = color, style = Fill)
        }
    }

    @Composable
    fun PlayTriangleOutline(modifier: Modifier = Modifier, color: Color = Color.Black, size: Dp = 16.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.12f
            val triangle = Path().apply {
                moveTo(w * 0.28f, h * 0.22f)
                lineTo(w * 0.78f, h * 0.50f)
                lineTo(w * 0.28f, h * 0.78f)
                close()
            }
            drawPath(
                triangle,
                color = color,
                style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

    @Composable
    fun Plus(modifier: Modifier = Modifier, color: Color = FocusColors.TextSecondary, size: Dp = 16.dp) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.12f
            drawLine(
                color = color,
                start = Offset(w * 0.5f, h * 0.15f),
                end = Offset(w * 0.5f, h * 0.85f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(w * 0.15f, h * 0.5f),
                end = Offset(w * 0.85f, h * 0.5f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }

    @Composable
    fun TaskEmptyIllustration(
        modifier: Modifier = Modifier,
        slateColor: Color = Color(0xFF555866),
        starColor: Color = FocusColors.MomentumYellow,
        size: Dp = 140.dp
    ) {
        Canvas(modifier = modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            // Board dimensions
            val boardW = w * 0.44f
            val boardH = h * 0.58f
            val bLeft = cx - boardW / 2f
            val bTop = cy - boardH / 2f + h * 0.035f
            val bRight = bLeft + boardW
            val bBottom = bTop + boardH
            val cornerR = boardW * 0.18f
            val strokeW = w * 0.016f

            val strokeStyle = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)

            // Clip dimensions
            val clipW = boardW * 0.38f
            val clipH = boardH * 0.12f
            val clipLeft = cx - clipW / 2f
            val clipRight = cx + clipW / 2f
            val clipTop = bTop - clipH * 0.40f
            val clipBottom = clipTop + clipH

            // Draw board outline (with opening at top where the clip is attached)
            val boardPath = Path().apply {
                moveTo(clipRight + w * 0.01f, bTop)
                lineTo(bRight - cornerR, bTop)
                quadraticTo(bRight, bTop, bRight, bTop + cornerR)
                lineTo(bRight, bBottom - cornerR)
                quadraticTo(bRight, bBottom, bRight - cornerR, bBottom)
                lineTo(bLeft + cornerR, bBottom)
                quadraticTo(bLeft, bBottom, bLeft, bBottom - cornerR)
                lineTo(bLeft, bTop + cornerR)
                quadraticTo(bLeft, bTop, bLeft + cornerR, bTop)
                lineTo(clipLeft - w * 0.01f, bTop)
            }
            drawPath(boardPath, color = slateColor, style = strokeStyle)

            // Draw clip
            val clipPath = Path().apply {
                val tabR = clipW * 0.18f
                moveTo(clipLeft, clipBottom)
                lineTo(clipLeft, clipTop + tabR)
                quadraticTo(clipLeft, clipTop, clipLeft + tabR, clipTop)

                // Small arch handle on top of clip
                val loopW = clipW * 0.52f
                val loopH = clipH * 0.55f
                val loopLeft = cx - loopW / 2f
                val loopRight = cx + loopW / 2f
                val loopR = loopW * 0.25f

                lineTo(loopLeft, clipTop)
                lineTo(loopLeft, clipTop - loopH + loopR)
                quadraticTo(loopLeft, clipTop - loopH, loopLeft + loopR, clipTop - loopH)
                lineTo(loopRight - loopR, clipTop - loopH)
                quadraticTo(loopRight, clipTop - loopH, loopRight, clipTop - loopH + loopR)
                lineTo(loopRight, clipTop)

                lineTo(clipRight - tabR, clipTop)
                quadraticTo(clipRight, clipTop, clipRight, clipTop + tabR)
                lineTo(clipRight, clipBottom)
            }
            drawPath(clipPath, color = slateColor, style = strokeStyle)

            // 3 Rows of items inside clipboard
            val rowStartY = bTop + boardH * 0.30f
            val rowSpacing = boardH * 0.19f
            val dotRadius = boardW * 0.038f
            val dotX = bLeft + boardW * 0.22f
            val lineStartX = bLeft + boardW * 0.35f

            for (i in 0 until 3) {
                val y = rowStartY + i * rowSpacing
                // Hollow bullet dot
                drawCircle(
                    color = slateColor,
                    radius = dotRadius,
                    center = Offset(dotX, y),
                    style = Stroke(width = strokeW * 0.95f)
                )
                // Task line
                val lineEndX = if (i == 2) bRight - boardW * 0.26f else bRight - boardW * 0.18f
                drawLine(
                    color = slateColor,
                    start = Offset(lineStartX, y),
                    end = Offset(lineEndX, y),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }

            // 4-point sparkle star drawer
            fun drawSparkle(center: Offset, radius: Float) {
                val path = Path().apply {
                    moveTo(center.x, center.y - radius)
                    quadraticTo(center.x, center.y, center.x + radius, center.y)
                    quadraticTo(center.x, center.y, center.x, center.y + radius)
                    quadraticTo(center.x, center.y, center.x - radius, center.y)
                    quadraticTo(center.x, center.y, center.x, center.y - radius)
                    close()
                }
                drawPath(path, color = starColor, style = Fill)
            }

            // 5 Sparkles positioned around the clipboard exactly like the reference UI
            // 1. Top-right
            drawSparkle(Offset(cx + boardW * 0.62f, cy - boardH * 0.54f), w * 0.046f)
            // 2. Mid-left
            drawSparkle(Offset(cx - boardW * 0.74f, cy - boardH * 0.12f), w * 0.040f)
            // 3. Bottom-left
            drawSparkle(Offset(cx - boardW * 0.70f, cy + boardH * 0.48f), w * 0.040f)
            // 4. Bottom-right
            drawSparkle(Offset(cx + boardW * 0.65f, cy + boardH * 0.48f), w * 0.040f)
            // 5. Mid-right (smaller)
            drawSparkle(Offset(cx + boardW * 0.75f, cy + boardH * 0.06f), w * 0.028f)
        }
    }
}
