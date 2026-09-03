package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import focus.domain.Priority

/**
 * Task input form matching the UI mock:
 * - "Add a task" header
 * - "What do you want to do?" text field
 * - Duration selector chips: 5m, 10m, 15m, 25m, 45m, 60m
 * - Priority selector chips: Low, Medium, High
 * - "Add Task" button
 */
@Composable
fun TaskInput(
    onAddTask: (title: String, durationMinutes: Int, priority: Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableIntStateOf(25) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    val focusRequester = remember { FocusRequester() }

    val durations = listOf(5, 10, 20, 30, 45, 60)

    Column(modifier = modifier.fillMaxWidth()) {
        
        // Text input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = {
                Text(
                    "What do you want to do?",
                    color = FocusColors.TextMuted
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FocusColors.Yellow,
                unfocusedBorderColor = FocusColors.GlassBorder,
                cursorColor = FocusColors.Yellow,
                focusedContainerColor = FocusColors.CardBackground,
                unfocusedContainerColor = FocusColors.CardBackground,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (title.isNotBlank()) {
                        onAddTask(title.trim(), selectedDuration, selectedPriority)
                        title = ""
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Duration and Priority row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Duration selector
            Column {
                Text(
                    text = "DURATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = FocusColors.TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    durations.forEach { duration ->
                        ChipButton(
                            text = "${duration}m",
                            isSelected = selectedDuration == duration,
                            onClick = { selectedDuration = duration }
                        )
                    }
                }
            }

            // Priority selector
            Column {
                Text(
                    text = "PRIORITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = FocusColors.TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Priority.entries.forEach { priority ->
                        ChipButton(
                            text = priority.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            isSelected = selectedPriority == priority,
                            onClick = { selectedPriority = priority }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

/**
 * Small chip/toggle button used for duration and priority selectors.
 */
@Composable
fun ChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) FocusColors.Yellow else Color.Transparent
    val textColor = if (isSelected) Color.Black else Color.White
    val borderColor = if (isSelected) FocusColors.Yellow else FocusColors.GlassBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

