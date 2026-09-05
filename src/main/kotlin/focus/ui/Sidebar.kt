package focus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Navigation sidebar matching the Momentum mock.
 */
@Composable
fun Sidebar(
    currentRoute: String = "Dashboard",
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("Dashboard") { color -> MomentumIcons.Dashboard(color = color, size = 18.dp) },
        NavItem("Insights") { color -> MomentumIcons.Insights(color = color, size = 18.dp) },
        NavItem("Sessions") { color -> MomentumIcons.Sessions(color = color, size = 18.dp) },
        NavItem("Tasks") { color -> MomentumIcons.Tasks(color = color, size = 18.dp) },
        NavItem("Tags") { color -> MomentumIcons.Tags(color = color, size = 18.dp) }
    )

    Column(
        modifier = modifier
            .width(210.dp)
            .fillMaxHeight()
            .background(FocusColors.SidebarBackground)
            .padding(horizontal = 14.dp, vertical = 26.dp)
    ) {
        // Logo Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 6.dp, bottom = 32.dp)
        ) {
            MomentumIcons.Logo(size = 32.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "momentum",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = (-0.3).sp
            )
        }

        // Navigation Items
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute.equals(item.name, ignoreCase = true)
                SidebarItemRow(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.name) }
                )
            }
        }
    }
}

private data class NavItem(
    val name: String,
    val icon: @Composable (Color) -> Unit
)

@Composable
private fun SidebarItemRow(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isSelected -> FocusColors.SidebarActiveTab
        isHovered -> Color.White.copy(alpha = 0.05f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isSelected -> FocusColors.MomentumYellow
        isHovered -> Color.White
        else -> FocusColors.TextSecondary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp)
    ) {
        item.icon(contentColor)
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = item.name,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
