package com.querycubix.alarmclock

import android.Manifest
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var alarmAdapter: AlarmAdapter
    private var alarms: MutableList<Alarm> = mutableListOf()
    private var pendingAlarm: Alarm? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Notification permission is required for alarms", Toast.LENGTH_SHORT).show()
        }
    }

    private val ringtonePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            pendingAlarm?.let { alarm ->
                alarm.toneUri = uri?.toString()
                saveAndScheduleAlarm(alarm)
            }
        } else {
            pendingAlarm?.let { saveAndScheduleAlarm(it) }
        }
        pendingAlarm = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkNotificationPermission()

        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val sdf = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        dateTextView.text = sdf.format(Date())

        alarms = AlarmStorage.getAlarms(this)
        
        val recyclerView = findViewById<RecyclerView>(R.id.alarmsRecyclerView)
        alarmAdapter = AlarmAdapter(
            alarms,
            onToggle = { alarm, isEnabled ->
                alarm.isEnabled = isEnabled
                AlarmStorage.updateAlarm(this, alarm)
                if (isEnabled) {
                    AlarmManagerHelper.scheduleAlarm(this, alarm)
                    Toast.makeText(this, "Alarm set for ${alarm.timeFormatted}", Toast.LENGTH_SHORT).show()
                } else {
                    AlarmManagerHelper.cancelAlarm(this, alarm)
                    Toast.makeText(this, "Alarm disabled", Toast.LENGTH_SHORT).show()
                }
            },
            onDelete = { alarm ->
                showDeleteConfirmation(alarm)
            }
        )
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = alarmAdapter

        findViewById<FloatingActionButton>(R.id.addAlarmFab).setOnClickListener {
            showTimePicker()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            pendingAlarm = Alarm(
                id = (System.currentTimeMillis() % 10000).toInt(),
                hour = selectedHour,
                minute = selectedMinute,
                isEnabled = true
            )
            showRingtonePicker()
        }, hour, minute, false).show()
    }

    private fun showRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone")
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        }
        ringtonePickerLauncher.launch(intent)
    }

    private fun saveAndScheduleAlarm(alarm: Alarm) {
        alarms.add(alarm)
        AlarmStorage.saveAlarms(this, alarms)
        alarmAdapter.updateAlarms(alarms.toList())
        AlarmManagerHelper.scheduleAlarm(this, alarm)
        Toast.makeText(this, "Alarm added for ${alarm.timeFormatted}", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmation(alarm: Alarm) {
        AlertDialog.Builder(this)
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Delete") { _, _ ->
                AlarmManagerHelper.cancelAlarm(this, alarm)
                alarms.remove(alarm)
                AlarmStorage.saveAlarms(this, alarms)
                alarmAdapter.updateAlarms(alarms.toList())
                Toast.makeText(this, "Alarm deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
