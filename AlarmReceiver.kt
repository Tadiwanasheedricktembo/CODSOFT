package com.querycubix.alarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val toneUri = intent.getStringExtra("ALARM_TONE")
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_TONE", toneUri)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
