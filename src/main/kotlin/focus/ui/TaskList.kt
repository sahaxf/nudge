package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import focus.domain.Task

/**
 * Scrollable task list with "TODAY" header.
 * Tasks arrive pre-sorted from the repository.
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    selectedTaskId: Long? = null,
    onSelectTask: (Task) -> Unit = {},
    onPlayTask: (Task) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(modifier = modifier) {
        // "TODAY" header
        Text(
            text = "TASK LIST",
            style = MaterialTheme.typography.titleSmall,
            color = FocusColors.TextMuted,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
        )

        HorizontalDivider(
            color = FocusColors.GlassBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (tasks.isEmpty()) {
            Text(
                text = "No tasks yet. Add one above!",
                style = MaterialTheme.typography.bodyMedium,
                color = FocusColors.TextMuted,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = contentPadding
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        isSelected = task.id == selectedTaskId,
                        onSelect = { onSelectTask(task) },
                        onPlay = { onPlayTask(task) }
                    )
                }
            }
        }
    }
}

