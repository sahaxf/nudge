package focus.timer

import focus.domain.TimerState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Timestamp-based focus timer. The source of truth is timestamps, not a decrementing counter.
 *
 * Calculates:
 *   elapsed = now - startedAt - totalPausedDuration
 *   remaining = duration - elapsed
 *   progress = elapsed / duration
 *
 * This prevents timer drift across pauses and system sleeps.
 */
class FocusTimer(private val scope: CoroutineScope) {

    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var totalDuration: Duration = Duration.ZERO
    private var startedAt: Instant? = null
    private var pausedAt: Instant? = null
    private var totalPausedDuration: Duration = Duration.ZERO
    private var tickJob: Job? = null

    /**
     * Start a new timer for the given number of minutes.
     */
    fun start(durationMinutes: Int) {
        stop() // clean up any existing timer

        totalDuration = durationMinutes.minutes
        startedAt = Clock.System.now()
        pausedAt = null
        totalPausedDuration = Duration.ZERO

        startTicking()
    }

    /**
     * Pause the running timer.
     */
    fun pause() {
        if (_state.value !is TimerState.Running) return
        pausedAt = Clock.System.now()
        tickJob?.cancel()
        updateState()
    }

    /**
     * Resume a paused timer.
     */
    fun resume() {
        val paused = pausedAt ?: return
        if (_state.value !is TimerState.Paused) return

        totalPausedDuration += Clock.System.now() - paused
        pausedAt = null

        startTicking()
    }

    /**
     * Toggle pause/resume.
     */
    fun togglePause() {
        when (_state.value) {
            is TimerState.Running -> pause()
            is TimerState.Paused -> resume()
            else -> {} // ignore
        }
    }

    /**
     * Stop the timer and reset to idle.
     */
    fun stop() {
        tickJob?.cancel()
        tickJob = null
        startedAt = null
        pausedAt = null
        totalPausedDuration = Duration.ZERO
        totalDuration = Duration.ZERO
        _state.value = TimerState.Idle
    }

    /**
     * Force-complete the timer (user pressed Enter).
     */
    fun complete() {
        tickJob?.cancel()
        tickJob = null
        _state.value = TimerState.Completed
    }

    private fun startTicking() {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                updateState()
                if (_state.value is TimerState.Completed) break
                delay(100) // ~100ms for smooth progress, display updates each second
            }
        }
    }

    private fun updateState() {
        val start = startedAt ?: return

        val elapsed = if (pausedAt != null) {
            pausedAt!! - start - totalPausedDuration
        } else {
            Clock.System.now() - start - totalPausedDuration
        }

        val remaining = (totalDuration - elapsed).coerceAtLeast(Duration.ZERO)
        val progress = (elapsed / totalDuration).toFloat().coerceIn(0f, 1f)
        val displayTime = formatDuration(remaining)

        _state.value = if (remaining <= Duration.ZERO) {
            tickJob?.cancel()
            TimerState.Completed
        } else if (pausedAt != null) {
            TimerState.Paused(remaining, progress, displayTime)
        } else {
            TimerState.Running(remaining, progress, displayTime)
        }
    }

    companion object {
        /**
         * Format a duration as "MM:SS".
         */
        fun formatDuration(duration: Duration): String {
            val totalSeconds = duration.inWholeSeconds.coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
    }
}

