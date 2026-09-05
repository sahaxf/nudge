package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import focus.domain.Priority

/**
 * Task input bar matching the modern Momentum design:
 * - Clean dark container card with subtle border
 * - "What do you want to focus on?" placeholder
 * - Duration selector pill (25 min v) with dropdown
 * - Tag selector pill (Select tags v) with dropdown
 * - Muted "Press Enter to add" helper text
 */
@Composable
fun TaskInput(
    onAddTask: (title: String, durationMinutes: Int, priority: Priority, tag: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(25) }
    var hasExplicitDuration by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var isDurationMenuOpen by remember { mutableStateOf(false) }
    var isTagMenuOpen by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    fun submitTask() {
        val trimmed = text.trim()
        if (trimmed.isNotBlank()) {
            val (cleanTitle, duration, tag) = parseTaskInput(trimmed)
            val finalDuration = if (hasExplicitDuration) selectedDuration else duration
            val finalTag = selectedTag ?: tag
            onAddTask(cleanTitle, finalDuration, Priority.MEDIUM, finalTag)
            text = ""
            selectedDuration = 25
            hasExplicitDuration = false
            selectedTag = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF141416), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF26262B), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Text Input Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = "What do you want to focus on?",
                        color = Color(0xFF71717A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = GoogleSansFontFamily
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitTask() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                                submitTask()
                                true
                            } else {
                                false
                            }
                        }
                )
            }

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Duration and Tag selector pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Duration Selector Pill
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B1B1E))
                                .border(1.dp, Color(0xFF2C2C32), RoundedCornerShape(8.dp))
                                .clickable { isDurationMenuOpen = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            MomentumIcons.Clock(
                                color = Color(0xFFA1A1AA),
                                size = 14.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$selectedDuration min",
                                color = Color(0xFFE4E4E7),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            MomentumIcons.ChevronDown(
                                color = Color(0xFFA1A1AA),
                                size = 10.dp
                            )
                        }

                        DropdownMenu(
                            expanded = isDurationMenuOpen,
                            onDismissRequest = { isDurationMenuOpen = false },
                            modifier = Modifier
                                .background(Color(0xFF1B1B1E))
                                .border(1.dp, Color(0xFF2C2C32), RoundedCornerShape(8.dp))
                        ) {
                            listOf(15, 25, 30, 45, 60, 90).forEach { mins ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "$mins min",
                                            color = if (selectedDuration == mins && hasExplicitDuration) FocusColors.MomentumYellow else Color.White,
                                            fontSize = 13.sp
                                        )
                                    },
                                    onClick = {
                                        selectedDuration = mins
                                        hasExplicitDuration = true
                                        isDurationMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Tag Selector Pill
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B1B1E))
                                .border(1.dp, Color(0xFF2C2C32), RoundedCornerShape(8.dp))
                                .clickable { isTagMenuOpen = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            MomentumIcons.Tag(
                                color = Color(0xFFA1A1AA),
                                size = 14.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedTag ?: "Select tags",
                                color = if (selectedTag != null) Color(0xFFE4E4E7) else Color(0xFFA1A1AA),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            MomentumIcons.ChevronDown(
                                color = Color(0xFFA1A1AA),
                                size = 10.dp
                            )
                        }

                        DropdownMenu(
                            expanded = isTagMenuOpen,
                            onDismissRequest = { isTagMenuOpen = false },
                            modifier = Modifier
                                .background(Color(0xFF1B1B1E))
                                .border(1.dp, Color(0xFF2C2C32), RoundedCornerShape(8.dp))
                        ) {
                            listOf(
                                "Learning",
                                "Work",
                                "Health",
                                "Personal",
                                "Deep Work",
                                "Writing",
                                "Planning",
                                "Admin"
                            ).forEach { tagOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = tagOption,
                                            color = if (selectedTag == tagOption) FocusColors.MomentumYellow else Color.White,
                                            fontSize = 13.sp
                                        )
                                    },
                                    onClick = {
                                        selectedTag = tagOption
                                        isTagMenuOpen = false
                                    }
                                )
                            }
                            if (selectedTag != null) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Clear tag",
                                            color = Color(0xFFEF4444),
                                            fontSize = 12.sp
                                        )
                                    },
                                    onClick = {
                                        selectedTag = null
                                        isTagMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Right: "Press Enter to add"
                Text(
                    text = "Press Enter to add",
                    color = Color(0xFF71717A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

/**
 * Parses user input for optional duration or tag hints, otherwise applies sensible defaults.
 * Examples:
 * - "Read Chapter 5 30 min" -> ("Read Chapter 5", 30, "Learning")
 * - "Implement user auth 60 min" -> ("Implement user auth", 60, "Work")
 * - "Go for a walk 20m" -> ("Go for a walk", 20, "Health")
 * - "Practice guitar 30m" -> ("Practice guitar", 30, "Personal")
 */
private fun parseTaskInput(raw: String): Triple<String, Int, String> {
    var title = raw
    var duration = 25

    // Check for trailing duration pattern like "45m", "30 min", "1h", "1h 30m"
    val durationRegex = Regex("""(?i)\s+(\d+h(?:\s*\d+(?:m|min|mins)?)?|\d+\s*(?:m|min|mins))\s*$""")
    val match = durationRegex.find(raw)
    if (match != null) {
        title = raw.substring(0, match.range.first).trim()
        val durationStr = match.groupValues[1].lowercase()
        duration = parseDurationString(durationStr)
    }

    // Infer tag from keywords
    val lowerTitle = title.lowercase()
    val tag = when {
        lowerTitle.contains("learn") || lowerTitle.contains("study") || lowerTitle.contains("read") || lowerTitle.contains(
            "book"
        ) || lowerTitle.contains("chapter") -> "Learning"

        lowerTitle.contains("health") || lowerTitle.contains("walk") || lowerTitle.contains("run") || lowerTitle.contains(
            "gym"
        ) || lowerTitle.contains("workout") -> "Health"

        lowerTitle.contains("person") || lowerTitle.contains("guitar") || lowerTitle.contains("music") || lowerTitle.contains(
            "hobby"
        ) || lowerTitle.contains("life") -> "Personal"

        lowerTitle.contains("auth") || lowerTitle.contains("code") || lowerTitle.contains("bug") || lowerTitle.contains(
            "dev"
        ) || lowerTitle.contains("work") -> "Work"

        lowerTitle.contains("write") || lowerTitle.contains("draft") || lowerTitle.contains("blog") -> "Writing"
        lowerTitle.contains("plan") || lowerTitle.contains("roadmap") || lowerTitle.contains("design") -> "Planning"
        lowerTitle.contains("email") || lowerTitle.contains("admin") || lowerTitle.contains("meeting") || lowerTitle.contains(
            "reply"
        ) -> "Admin"

        else -> "Deep Work"
    }

    return Triple(title, duration, tag)
}

private fun parseDurationString(str: String): Int {
    return try {
        val clean = str.replace("mins", "").replace("min", "").trim()
        if (clean.contains("h")) {
            val parts = clean.split("h")
            val hours = parts[0].trim().toIntOrNull() ?: 1
            val mins = if (parts.size > 1 && parts[1].isNotBlank()) {
                parts[1].replace("m", "").trim().toIntOrNull() ?: 0
            } else 0
            hours * 60 + mins
        } else {
            clean.replace("m", "").trim().toIntOrNull() ?: 25
        }
    } catch (_: Exception) {
        25
    }
}
