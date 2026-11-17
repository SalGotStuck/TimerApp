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
    val totalSeconds: Int = 60,
    val remainingSeconds: Int = 60,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
) {
    val display: String
        get() {
            val m = remainingSeconds / 60
            val s = remainingSeconds % 60
            return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
        }
}

class TimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()
    private var job: Job? = null

    fun addSeconds(delta: Int) {
        if (_state.value.isRunning) return
        val new = (_state.value.totalSeconds + delta).coerceIn(0, 99 * 60)
        _state.value = TimerState(totalSeconds = new, remainingSeconds = new)
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

    fun reset() {
        pause()
        _state.update { it.copy(remainingSeconds = it.totalSeconds, isFinished = false) }
    }
}