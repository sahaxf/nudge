package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.domain.Task
import focus.domain.TaskStatus

/**
 * Task list section matching the Momentum mock:
 * - Header row with "All tasks" on left, Filter ("All ⌵") & Sort ("≡ Sort") on right
 * - Enclosed dark card container with rounded borders
 * - Task items with dividers
 * - Floating "Start focusing" button over the bottom of the task list
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    selectedTaskId: Long? = null,
    onSelectTask: (Task) -> Unit = {},
    onToggleCompleted: (Task) -> Unit = {},
    onPlayTask: (Task) -> Unit = {},
    onDeleteTask: (Long) -> Unit = {},
    onStartFocus: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var filterType by remember { mutableStateOf("All") }
    var sortType by remember { mutableStateOf("Default") }
    var isFilterMenuOpen by remember { mutableStateOf(false) }
    var isSortMenuOpen by remember { mutableStateOf(false) }

    // Apply filtering
    val filteredTasks = when (filterType) {
        "Active" -> tasks.filter { it.status != TaskStatus.COMPLETED }
        "Completed" -> tasks.filter { it.status == TaskStatus.COMPLETED }
        else -> tasks
    }

    // Apply sorting
    val displayedTasks = when (sortType) {
        "Duration" -> filteredTasks.sortedByDescending { it.durationMinutes }
        "Title" -> filteredTasks.sortedBy { it.title.lowercase() }
        else -> filteredTasks
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Filter Dropdown
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { isFilterMenuOpen = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = filterType,
                            color = FocusColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        MomentumIcons.ChevronDown(color = FocusColors.TextSecondary, size = 12.dp)
                    }

                    DropdownMenu(
                        expanded = isFilterMenuOpen,
                        onDismissRequest = { isFilterMenuOpen = false },
                        modifier = Modifier.background(FocusColors.CardBackground)
                    ) {
                        listOf("All", "Active", "Completed").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    filterType = option
                                    isFilterMenuOpen = false
                                }
                            )
                        }
                    }
                }

                // Separator line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(FocusColors.CardBorder)
                )

                // Sort Dropdown
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { isSortMenuOpen = true }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        MomentumIcons.Sort(color = FocusColors.TextSecondary, size = 13.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sort",
                            color = FocusColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    DropdownMenu(
                        expanded = isSortMenuOpen,
                        onDismissRequest = { isSortMenuOpen = false },
                        modifier = Modifier.background(FocusColors.CardBackground)
                    ) {
                        listOf("Default", "Duration", "Title").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    sortType = option
                                    isSortMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Tasks Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FocusColors.CardBackground, RoundedCornerShape(16.dp))
                        .border(1.dp, FocusColors.CardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        MomentumIcons.TaskEmptyIllustration(
                            size = 136.dp,
                            slateColor = Color(0xFF525666),
                            starColor = FocusColors.MomentumYellow
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "No tasks yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = (-0.2).sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Add a task above to get started.",
                            color = FocusColors.TextSecondary,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            } else if (displayedTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FocusColors.CardBackground, RoundedCornerShape(16.dp))
                        .border(1.dp, FocusColors.CardBorder, RoundedCornerShape(16.dp))
                        .padding(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks matching filter.",
                        color = FocusColors.TextMuted,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 2.dp, bottom = 84.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(displayedTasks, key = { _, task -> task.id }) { _, task ->
                        TaskRow(
                            task = task,
                            isSelected = task.id == selectedTaskId,
                            onSelect = { onSelectTask(task) },
                            onToggleCompleted = { onToggleCompleted(task) },
                            onPlay = { onPlayTask(task) },
                            onDelete = { onDeleteTask(task.id) }
                        )
                    }
                }
            }

            // Floating "Start focusing" button over the bottom of the task list
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onStartFocus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusColors.MomentumYellow,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 2.dp
                    ),
                    modifier = Modifier
                        .widthIn(min = 340.dp, max = 500.dp)
                        .fillMaxWidth(0.66f)
                        .height(44.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MomentumIcons.PlayTriangleOutline(
                            color = Color.Black,
                            size = 18.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Start focusing",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
