package com.example.timerapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AudioToolbox.AudioServicesPlaySystemSound

@Composable
actual fun rememberAlarm(): Alarm = remember {
    object : Alarm {
        override fun play() { AudioServicesPlaySystemSound(1005u) }
        override fun stop() {}
    }
}