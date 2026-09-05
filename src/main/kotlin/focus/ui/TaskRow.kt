package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.domain.Task
import focus.domain.TaskStatus

/**
 * Task item row matching the modern Momentum design:
 * - Rounded card container with subtle border
 * - Circle checkbox on the left
 * - Task title on top
 * - Clock icon, duration (e.g. "30 min"), bullet dot, and colored category tag pill on the bottom row
 * - Action icons on the right: edit pencil icon and delete trash icon
 */
@Composable
fun TaskRow(
    task: Task,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onToggleCompleted: () -> Unit = {},
    onPlay: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val playInteractionSource = remember { MutableInteractionSource() }
    val isPlayHovered by playInteractionSource.collectIsHoveredAsState()
    var isMenuOpen by remember { mutableStateOf(false) }

    val moreInteractionSource = remember { MutableInteractionSource() }
    val isMoreHovered by moreInteractionSource.collectIsHoveredAsState()

    val isCompleted = task.status == TaskStatus.COMPLETED

    val backgroundColor = when {
        isSelected -> Color(0xFF1E1E24)
        isHovered -> Color(0xFF19191D)
        else -> Color(0xFF141416)
    }

    val borderColor = when {
        isSelected -> Color(0xFF454552)
        isHovered -> Color(0xFF454552) // Color(0xFF33333D)
        else -> Color(0xFF24242A)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // 1. Circle Checkbox
        Box(
            modifier = Modifier
                .size(25.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggleCompleted
                ),
            contentAlignment = Alignment.Center
        ) {
            MomentumIcons.CircleCheckbox(
                checked = isCompleted,
                size = 22.dp,
                uncheckedColor = if (isHovered) Color(0xFF555562) else Color(0xFF3E3E48)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Title + Details (Clock, Duration, Dot, Category Badge)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = task.title,
                color = if (isCompleted) FocusColors.TextDim else Color(0xFFF2F2F4),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clock icon
                MomentumIcons.Clock(
                    color = Color(0xFF8E8E98),
                    size = 12.dp
                )

                Spacer(modifier = Modifier.width(5.dp))

                // Duration text (e.g. "30 min")
                Text(
                    text = formatDuration(task.durationMinutes),
                    color = Color(0xFF8E8E98),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(7.dp))

                // Bullet dot
                Text(
                    text = "•",
                    color = Color(0xFF52525B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(7.dp))

                // Category pill
                TagChip(tag = task.tag)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 3. Right Side Info (Duration, Play Button, Three Dots)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // // Duration text (e.g. "30 min")
            // Text(
            //     text = formatDuration(task.durationMinutes),
            //     color = Color(0xFF8E8E98),
            //     fontSize = 12.5.sp,
            //     fontWeight = FontWeight.Normal
            // )

            // Play button (starts focus mode)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    // .border(
                    //     1.dp,
                    //     Color(0xFF33333D),
                    //     RoundedCornerShape(20.dp)
                    // )
                    .clickable(
                        interactionSource = playInteractionSource,
                        indication = null,
                        onClick = onPlay
                    ),
                contentAlignment = Alignment.Center
            ) {
                MomentumIcons.PlayTriangle(
                    color = if (isPlayHovered) Color.White else Color(0xFF8E8E98),
                    size = if (isPlayHovered) 22.dp else 20.dp
                )
            }

            // Three dot Button
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isMoreHovered) Color(0xFF26262E) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable(
                            interactionSource = moreInteractionSource,
                            indication = null,
                            onClick = { isMenuOpen = true }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MomentumIcons.MoreHorizontal(
                        color = if (isMoreHovered) Color.White else FocusColors.TextSecondary,
                        size = 15.dp
                    )
                }

                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false },
                    modifier = Modifier.background(FocusColors.CardBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("Start focus", color = Color.White, fontSize = 13.sp) },
                        onClick = {
                            isMenuOpen = false
                            onPlay()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isCompleted) "Mark as incomplete" else "Mark as complete",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            isMenuOpen = false
                            onToggleCompleted()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete task", color = FocusColors.Red, fontSize = 13.sp) },
                        onClick = {
                            isMenuOpen = false
                            onDelete()
                        }
                    )
                }
            }

        }
    }
}

/**
 * Tag badge chip styled according to tag category matching the Momentum design:
 * - Learning: Soft purple
 * - Work: Soft blue
 * - Health: Soft green
 * - Personal: Soft orange
 */
@Composable
fun TagChip(tag: String, modifier: Modifier = Modifier) {
    val (bg, textColor) = getTagColors(tag)

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(5.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tag,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

private fun getTagColors(tag: String): Pair<Color, Color> {
    val clean = tag.lowercase().trim()
    return when {
        clean.contains("learn") || clean.contains("study") || clean.contains("read") || clean.contains("book") || clean.contains(
            "chapter"
        ) ->
            Pair(Color(0xFF261D36), Color(0xFFB392F0))

        clean.contains("work") || clean.contains("auth") || clean.contains("code") || clean.contains("dev") ->
            Pair(Color(0xFF142238), Color(0xFF5296E8))

        clean.contains("health") || clean.contains("walk") || clean.contains("gym") || clean.contains("fitness") || clean.contains(
            "exercise"
        ) ->
            Pair(Color(0xFF14291B), Color(0xFF4DBE6E))

        clean.contains("person") || clean.contains("guitar") || clean.contains("music") || clean.contains("hobby") || clean.contains(
            "life"
        ) ->
            Pair(Color(0xFF332014), Color(0xFFE88A3C))

        clean.contains("deep") ->
            Pair(FocusColors.TagDeepWorkBg, FocusColors.TagDeepWorkText)

        clean.contains("writ") || clean.contains("blog") ->
            Pair(FocusColors.TagWritingBg, FocusColors.TagWritingText)

        clean.contains("plan") || clean.contains("design") ->
            Pair(FocusColors.TagPlanningBg, FocusColors.TagPlanningText)

        clean.contains("admin") || clean.contains("email") ->
            Pair(FocusColors.TagAdminBg, FocusColors.TagAdminText)

        else ->
            Pair(FocusColors.TagDefaultBg, FocusColors.TagDefaultText)
    }
}

private fun formatDuration(minutes: Int): String {
    return if (minutes >= 120 && minutes % 60 == 0) {
        "${minutes / 60}h"
    } else if (minutes > 180) {
        val h = minutes / 60
        val m = minutes % 60
        if (m > 0) "${h}h ${m}m" else "${h}h"
    } else {
        "${minutes} min"
    }
}
