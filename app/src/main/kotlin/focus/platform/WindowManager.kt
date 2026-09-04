package focus.platform

import java.awt.GraphicsEnvironment
import java.awt.Toolkit

import focus.ui.FocusPill

/**
 * Utility for window positioning on the primary display.
 */
object WindowManager {

    private const val BOTTOM_MARGIN_PX = 32
    private const val WINDOW_WIDTH = 340
    private const val WINDOW_HEIGHT = 64

    data class WindowPosition(val x: Int, val y: Int)

    /**
     * Calculate the bottom-center position for the focus pill window.
     */
    fun getFocusPillPosition(): WindowPosition {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val bounds = ge.defaultScreenDevice.defaultConfiguration.bounds
        val insets = Toolkit.getDefaultToolkit().getScreenInsets(
            ge.defaultScreenDevice.defaultConfiguration
        )

        val usableHeight = bounds.height - insets.bottom

        val x = bounds.x + (bounds.width - WINDOW_WIDTH) / 2
        val y = bounds.y + usableHeight - WINDOW_HEIGHT - BOTTOM_MARGIN_PX

        return WindowPosition(x, y)
    }

    /**
     * Get the screen dimensions.
     */
    fun getScreenSize(): Pair<Int, Int> {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        return Pair(screenSize.width, screenSize.height)
    }
}

