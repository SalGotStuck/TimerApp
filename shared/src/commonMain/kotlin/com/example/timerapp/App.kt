package com.example.timerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val timer = viewModel { TimerViewModel() }
        val state by timer.state.collectAsState()

        val alarm = rememberAlarm()
        LaunchedEffect(state.isFinished) {
            if (state.isFinished) alarm.play() else alarm.stop()
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(if (state.isFinished) "Time's up!" else state.display, fontSize = 64.sp)
            Spacer(Modifier.height(24.dp))

            // Scroll wheels to choose hours, minutes and seconds (locked while running).
            val canEdit = !state.isRunning
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                WheelPicker(state.hours, 0..23, "hours", canEdit) {
                    timer.setDuration(it, state.minutes, state.seconds)
                }
                Text(":", fontSize = 28.sp)
                WheelPicker(state.minutes, 0..59, "min", canEdit) {
                    timer.setDuration(state.hours, it, state.seconds)
                }
                Text(":", fontSize = 28.sp)
                WheelPicker(state.seconds, 0..59, "sec", canEdit) {
                    timer.setDuration(state.hours, state.minutes, it)
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { if (state.isRunning) timer.pause() else timer.start() },
                    enabled = state.isRunning || state.remainingSeconds > 0,
                ) {
                    Text(if (state.isRunning) "Pause" else "Start")
                }
                OutlinedButton(onClick = { timer.reset() }) { Text("Reset") }
            }
        }
    }
}