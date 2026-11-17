package com.example.timerapp

import androidx.compose.runtime.Composable

interface Alarm {
    fun play()
    fun stop()
}

@Composable
expect fun rememberAlarm(): Alarm