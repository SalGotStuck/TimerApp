package com.example.timerapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerState(
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
) {
    // Parts of the chosen duration, used by the scroll wheels.
    val hours: Int get() = totalSeconds / 3600
    val minutes: Int get() = (totalSeconds % 3600) / 60
    val seconds: Int get() = totalSeconds % 60

    // Countdown shown on screen, e.g. 0:00:00 or 1:05:30.
    val display: String
        get() {
            val h = remainingSeconds / 3600
            val m = (remainingSeconds % 3600) / 60
            val s = remainingSeconds % 60
            return "$h:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        }
}

class TimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()
    private var job: Job? = null

    fun setDuration(hours: Int, minutes: Int, seconds: Int) {
        if (_state.value.isRunning) return
        val total = hours * 3600 + minutes * 60 + seconds
        if (total == _state.value.totalSeconds) return
        _state.value = TimerState(totalSeconds = total, remainingSeconds = total)
    }

    fun start() {
        if (job?.isActive == true || _state.value.remainingSeconds == 0) return
        _state.update { it.copy(isRunning = true) }
        job = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0) {
                delay(1000)
                _state.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            _state.update { it.copy(isRunning = false, isFinished = true) }
        }
    }

    fun pause() {
        job?.cancel()
        _state.update { it.copy(isRunning = false) }
    }

    // Stops the timer and clears everything back to 0:00:00.
    fun reset() {
        pause()
        _state.value = TimerState()
    }
}