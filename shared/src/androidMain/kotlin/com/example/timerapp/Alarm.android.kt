package com.example.timerapp

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberAlarm(): Alarm {
    val context = LocalContext.current.applicationContext
    val alarm = remember(context) { AndroidAlarm(context) }
    DisposableEffect(alarm) { onDispose { alarm.stop() } }
    return alarm
}

private class AndroidAlarm(private val context: Context) : Alarm {
    private var ringtone: Ringtone? = null

    override fun play() {
        stop()
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(context, uri)?.also { it.play() }
    }

    override fun stop() {
        ringtone?.stop()
        ringtone = null
    }
}