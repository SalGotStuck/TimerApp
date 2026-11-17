package com.example.timerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { timer.addSeconds(-60) }) { Text("-1:00") }
                OutlinedButton(onClick = { timer.addSeconds(60) }) { Text("+1:00") }
            }


            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { timer.addSeconds(-10) }) { Text("-0:10") }
                OutlinedButton(onClick = { timer.addSeconds(10) }) {Text("+0:10") }
            }





            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { if (state.isRunning) timer.pause() else timer.start() }) {
                    Text(if (state.isRunning) "Pause" else "Start")
                }
                OutlinedButton(onClick = { timer.reset() }) { Text("Reset") }
            }
        }

    }
}