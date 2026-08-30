package focus.state

import focus.data.TaskRepository
import focus.domain.*
import focus.timer.FocusTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Which mode the app is in.
 */
enum class AppMode {
    PLANNING,
    FOCUS
}

/**
 * Central application state coordinator.
 * Bridges the UI, timer, and repository layers.
 */
class AppState(
    private val repository: TaskRepository,
    private val scope: CoroutineScope
) {
    val timer = FocusTimer(scope)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    private val _appMode = MutableStateFlow(AppMode.PLANNING)
    val appMode: StateFlow<AppMode> = _appMode.asStateFlow()

    private var currentSessionId: Long? = null

    init {
        refreshTasks()

        // Monitor timer state for auto-completion
        scope.launch {
            timer.state.collect { timerState ->
                if (timerState is TimerState.Completed) {
                    onTimerCompleted()
                }
            }
        }
    }

    /**
     * Add a new task and refresh the list.
     */
    fun addTask(title: String, durationMinutes: Int, priority: Priority) {
        scope.launch {
            repository.createTask(title, durationMinutes, priority)
            refreshTasksInternal()
        }
    }

    /**
     * Select a task for the next focus session.
     */
    fun selectTask(task: Task?) {
        _selectedTask.value = task
    }

    /**
     * Start a focus session with the selected task (or the highest-priority TODO task).
     */
    fun startFocus() {
        scope.launch {
            val task = _selectedTask.value
                ?: _tasks.value.firstOrNull { it.status == TaskStatus.TODO }
                ?: return@launch

            // Mark task as in-progress
            repository.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)

            // Record session
            val now = Clock.System.now()
            currentSessionId = repository.createSession(
                taskId = task.id,
                startedAt = now,
                plannedDurationSeconds = task.durationMinutes * 60
            )

            _selectedTask.value = task.copy(status = TaskStatus.IN_PROGRESS)
            _appMode.value = AppMode.FOCUS
            timer.start(task.durationMinutes)

            refreshTasksInternal()
        }
    }

    /**
     * Pause or resume the timer.
     */
    fun togglePause() {
        timer.togglePause()
    }

    /**
     * Force-complete the current task.
     */
    fun completeCurrentTask() {
        timer.complete()
    }

    /**
     * Stop the current session and return to planning.
     */
    fun stopSession() {
        scope.launch {
            val task = _selectedTask.value
            if (task != null) {
                // Revert task to TODO if stopping early
                repository.updateTaskStatus(task.id, TaskStatus.TODO)
            }
            timer.stop()
            currentSessionId = null
            _selectedTask.value = null
            _appMode.value = AppMode.PLANNING
            refreshTasksInternal()
        }
    }

    /**
     * Delete a task.
     */
    fun deleteTask(taskId: Long) {
        scope.launch {
            repository.deleteTask(taskId)
            refreshTasksInternal()
        }
    }

    /**
     * Called when timer naturally reaches zero.
     */
    private fun onTimerCompleted() {
        scope.launch {
            val task = _selectedTask.value ?: return@launch

            // Mark task completed
            repository.updateTaskStatus(task.id, TaskStatus.COMPLETED)

            // Complete session record
            val sessionId = currentSessionId
            if (sessionId != null) {
                repository.completeSession(
                    sessionId = sessionId,
                    endedAt = Clock.System.now(),
                    actualDurationSeconds = task.durationMinutes * 60
                )
            }

            currentSessionId = null

            // Brief delay for completion animation, then return to planning
            kotlinx.coroutines.delay(2000)
            _selectedTask.value = null
            _appMode.value = AppMode.PLANNING
            timer.stop()
            refreshTasksInternal()
        }
    }

    fun refreshTasks() {
        scope.launch {
            refreshTasksInternal()
        }
    }

    private suspend fun refreshTasksInternal() {
        _tasks.value = repository.getAllTasks()
    }
}

