package com.querycubix.alarmclock

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        setContentView(R.layout.activity_alarm_receiver)

        val timeTextView = findViewById<TextView>(R.id.alarmTimeTextView)
        val dismissButton = findViewById<Button>(R.id.dismissButton)
        val snoozeButton = findViewById<Button>(R.id.snoozeButton)

        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime

        dismissButton.setOnClickListener {
            stopAlarmService()
            finish()
        }

        snoozeButton.setOnClickListener {
            snoozeAlarm()
            stopAlarmService()
            finish()
        }
    }

    private fun stopAlarmService() {
        stopService(Intent(this, AlarmService::class.java))
    }

    private fun snoozeAlarm() {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }
        val alarm = Alarm(
            id = (System.currentTimeMillis() % 10000).toInt(),
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
            isEnabled = true
        )
        AlarmManagerHelper.scheduleAlarm(this, alarm)
    }
}
