package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
 * Featured "Up next" task card:
 * - Rounded card container with warm amber border (Color(0xFF4A3E1E))
 * - Dark background (Color(0xFF131316))
 * - Hollow circle checkbox on the left
 * - Task title on top
 * - Clock icon, duration ("25 min"), bullet "•", and colored category tag pill on the bottom row
 * - Prominent golden-yellow "▶ Start" button on the right
 */
@Composable
fun UpNextTaskCard(
    task: Task,
    onToggleCompleted: () -> Unit = {},
    onPlay: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val checkboxInteractionSource = remember { MutableInteractionSource() }
    val isCheckboxHovered by checkboxInteractionSource.collectIsHoveredAsState()

    var isMenuOpen by remember { mutableStateOf(false) }
    val isCompleted = task.status == TaskStatus.COMPLETED

    val borderColor = Color(0xFF4A3E1E)
    val backgroundColor = Color(0xFF131316)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // 1. Circle Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    interactionSource = checkboxInteractionSource,
                    indication = null,
                    onClick = onToggleCompleted
                ),
            contentAlignment = Alignment.Center
        ) {
            MomentumIcons.CircleCheckbox(
                checked = isCompleted,
                size = 22.dp,
                uncheckedColor = if (isCheckboxHovered) Color(0xFF555562) else Color(0xFF3F3F4A)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 2. Title + Details (Clock, Duration, Dot, Category Badge)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = task.title,
                color = if (isCompleted) Color(0xFF71717A) else Color(0xFFF2F2F4),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Clock icon
                MomentumIcons.Clock(
                    color = Color(0xFF8E8E98),
                    size = 12.dp
                )

                Spacer(modifier = Modifier.width(5.dp))

                // Duration text (e.g. "25 min")
                Text(
                    text = formatDuration(task.durationMinutes),
                    color = Color(0xFF8E8E98),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Bullet dot
                Text(
                    text = "•",
                    color = Color(0xFF52525B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Category pill
                TagChip(tag = task.tag)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 3. Prominent Yellow "Start" Button
        Button(
            onClick = onPlay,
            colors = ButtonDefaults.buttonColors(
                containerColor = FocusColors.MomentumYellow,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 1.dp
            ),
            modifier = Modifier.height(36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MomentumIcons.PlayTriangle(
                    color = Color.Black,
                    size = 11.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Start",
                    color = Color.Black,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Task item row for later tasks matching the design:
 * - Clean row layout without card border
 * - Circle checkbox on the left
 * - Task title and details in the center
 * - Play icon button and three dots menu on the right
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

    val checkboxInteractionSource = remember { MutableInteractionSource() }
    val isCheckboxHovered by checkboxInteractionSource.collectIsHoveredAsState()

    val playInteractionSource = remember { MutableInteractionSource() }
    val isPlayHovered by playInteractionSource.collectIsHoveredAsState()

    val moreInteractionSource = remember { MutableInteractionSource() }
    val isMoreHovered by moreInteractionSource.collectIsHoveredAsState()

    var isMenuOpen by remember { mutableStateOf(false) }
    val isCompleted = task.status == TaskStatus.COMPLETED

    val rowBg = if (isHovered) Color(0xFF16161A) else Color.Transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(rowBg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            )
            .padding(horizontal = 4.dp, vertical = 13.dp)
    ) {
        // 1. Circle Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    interactionSource = checkboxInteractionSource,
                    indication = null,
                    onClick = onToggleCompleted
                ),
            contentAlignment = Alignment.Center
        ) {
            MomentumIcons.CircleCheckbox(
                checked = isCompleted,
                size = 22.dp,
                uncheckedColor = if (isCheckboxHovered) Color(0xFF555562) else Color(0xFF3F3F4A)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 2. Title + Details (Clock, Duration, Dot, Category Badge)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = task.title,
                color = if (isCompleted) Color(0xFF71717A) else Color(0xFFF2F2F4),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
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

                Spacer(modifier = Modifier.width(6.dp))

                // Bullet dot
                Text(
                    text = "•",
                    color = Color(0xFF52525B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Category pill
                TagChip(tag = task.tag)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 3. Right Side Actions: Play Triangle & Three Dots Menu
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play Button (starts focus on this task)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(
                        interactionSource = playInteractionSource,
                        indication = null,
                        onClick = onPlay
                    ),
                contentAlignment = Alignment.Center
            ) {
                MomentumIcons.PlayTriangle(
                    color = if (isPlayHovered) Color.White else Color(0xFF8E8E98),
                    size = 13.dp
                )
            }

            // Three dots button
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(
                            interactionSource = moreInteractionSource,
                            indication = null,
                            onClick = { isMenuOpen = true }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MomentumIcons.MoreHorizontal(
                        color = if (isMoreHovered) Color.White else Color(0xFF8E8E98),
                        size = 16.dp
                    )
                }

                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false },
                    modifier = Modifier.background(Color(0xFF1A1A1E))
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
 * Tag badge chip styled according to tag category:
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

fun getTagColors(tag: String): Pair<Color, Color> {
    val clean = tag.lowercase().trim()
    return when {
        clean.contains("health") || clean.contains("walk") || clean.contains("gym") || clean.contains("fitness") || clean.contains("exercise") ->
            Pair(Color(0xFF14291B), Color(0xFF4DBE6E))

        clean.contains("learn") || clean.contains("study") || clean.contains("read") || clean.contains("book") || clean.contains("chapter") ->
            Pair(Color(0xFF261D36), Color(0xFFB392F0))

        clean.contains("person") || clean.contains("guitar") || clean.contains("music") || clean.contains("hobby") || clean.contains("life") ->
            Pair(Color(0xFF332014), Color(0xFFE88A3C))

        clean.contains("work") || clean.contains("auth") || clean.contains("code") || clean.contains("dev") ->
            Pair(Color(0xFF142238), Color(0xFF5296E8))

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

fun formatDuration(minutes: Int): String {
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
