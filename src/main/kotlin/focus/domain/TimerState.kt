package focus.domain

import kotlin.time.Duration

/**
 * Represents the current state of the focus timer.
 * Uses a sealed class for the state machine: IDLE → RUNNING ↔ PAUSED → COMPLETED
 */
sealed class TimerState {
    /** No active timer. */
    data object Idle : TimerState()

    /** Timer is actively counting down. */
    data class Running(
        val remaining: Duration,
        val progress: Float,     // 0.0 → 1.0 (fraction elapsed)
        val displayTime: String  // "MM:SS" formatted
    ) : TimerState()

    /** Timer is paused. */
    data class Paused(
        val remaining: Duration,
        val progress: Float,
        val displayTime: String
    ) : TimerState()

    /** Timer has completed (reached zero). */
    data object Completed : TimerState()
}

