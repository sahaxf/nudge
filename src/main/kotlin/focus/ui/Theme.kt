package focus.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

/**
 * Color palette from the UI mock.
 */
object FocusColors {
    // Primary yellow (progress fill, buttons)
    val Yellow = Color(0xFFFFD23F)
    val YellowDark = Color(0xFFE6BD38)
    val MomentumYellow = Color(0xFFF5C518)

    // Surfaces
    val DarkSurface = Color(0xFF2B2B2E)
    val DarkerBackground = Color(0xFF1A1A1D)
    val AppBackground = Color(0xFF0F0F12)
    val SidebarBackground = Color(0xFF0C0C0E)
    val SidebarActiveTab = Color(0xFF262010)
    val CardBackground = Color(0xFF141417)
    val InputBackground = Color(0xFF161619)
    val CardBorder = Color(0xFF232329)
    val DividerColor = Color(0xFF1C1C22)

    // Text
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFF9CA3AF)
    val TextMuted = Color(0xFF71717A)
    val TextDim = Color(0xFF52525B)

    // Tag colors
    val TagDeepWorkBg = Color(0xFF2B220C)
    val TagDeepWorkText = Color(0xFFF5C518)
    val TagWritingBg = Color(0xFF0F2B1D)
    val TagWritingText = Color(0xFF4ADE80)
    val TagPlanningBg = Color(0xFF0C243B)
    val TagPlanningText = Color(0xFF38BDF8)
    val TagAdminBg = Color(0xFF261435)
    val TagAdminText = Color(0xFFC084FC)
    val TagDefaultBg = Color(0xFF232328)
    val TagDefaultText = Color(0xFFD4D4D8)

    // Status
    val Green = Color(0xFF22C55E)
    val Red = Color(0xFFEF4444)

    // Priority badge colors
    val HighPriority = Color(0xFFEF4444)
    val MediumPriority = Color(0xFFFFD23F)
    val LowPriority = Color(0xFF8A8A8F)

    // Glass effect
    val GlassSurface = Color(0xFF1A1A1D).copy(alpha = 0.85f)
    val GlassBorder = Color(0xFFFFFFFF).copy(alpha = 0.1f)

    // Focus pill
    val PillBackground = Color(0xFF2B2B2E).copy(alpha = 0.9f)
    val PillProgress = Yellow
    val PillText = Color.White
    
    
    // ---------------------------------------------------------
    // Glass
    // ---------------------------------------------------------

    val GlassTop = Color(
        red = 0x30,
        green = 0x30,
        blue = 0x35,
        alpha = 0xB0
    )

    val GlassBottom = Color(
        red = 0x12,
        green = 0x12,
        blue = 0x16,
        alpha = 0xB8
    )

    // ---------------------------------------------------------
    // Progress
    // ---------------------------------------------------------

    val ProgressStart = Color(0xFFFFC83D)
    val ProgressEnd = Color(0xFFFFD966)
    
    val ProgressHighlight = Color.White.copy(alpha = 0.20f)

    // ---------------------------------------------------------
    // Border
    // ---------------------------------------------------------
    
    val BorderHighlight = Color.White.copy(alpha = 0.65f)
    val Border = Color.White.copy(alpha = 0.30f)

    // val BorderHighlight = Color.White.copy(
    //     alpha = 0.22f
    // )

    // val Border = Color.White.copy(
    //     alpha = 0.10f
    // )
}

private val DarkColors = darkColorScheme(
    primary = FocusColors.Yellow,
    onPrimary = Color.Black,
    surface = FocusColors.DarkSurface,
    onSurface = FocusColors.TextPrimary,
    background = FocusColors.DarkerBackground,
    onBackground = FocusColors.TextPrimary,
    surfaceVariant = FocusColors.CardBackground,
    onSurfaceVariant = FocusColors.TextMuted,
    outline = FocusColors.GlassBorder,
    secondaryContainer = FocusColors.DarkSurface,
    onSecondaryContainer = FocusColors.TextPrimary,
)

private val LightColors = lightColorScheme(
    primary = FocusColors.Yellow,
    onPrimary = Color.Black,
)

private val FocusTypography = Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun FocusTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FocusTypography,
        content = content
    )
}

