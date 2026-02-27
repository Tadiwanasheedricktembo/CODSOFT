package com.querycubix.alarmclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class RescheduleAlarmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("RescheduleReceiver", "Rescheduling alarms after boot")
            val alarms = AlarmStorage.getAlarms(context)
            for (alarm in alarms) {
                if (alarm.isEnabled) {
                    AlarmManagerHelper.scheduleAlarm(context, alarm)
                }
            }
        }
    }
}
