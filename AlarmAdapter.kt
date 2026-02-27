package com.querycubix.alarmclock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch

class AlarmAdapter(
    private var alarms: List<Alarm>,
    private val onToggle: (Alarm, Boolean) -> Unit,
    private val onDelete: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeTextView: TextView = view.findViewById(R.id.alarmTimeTextView)
        val daysTextView: TextView = view.findViewById(R.id.alarmDaysTextView)
        val alarmSwitch: MaterialSwitch = view.findViewById(R.id.alarmSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.timeTextView.text = alarm.timeFormatted
        holder.daysTextView.text = alarm.days
        
        holder.alarmSwitch.setOnCheckedChangeListener(null)
        holder.alarmSwitch.isChecked = alarm.isEnabled
        holder.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            onToggle(alarm, isChecked)
        }

        holder.itemView.setOnLongClickListener {
            onDelete(alarm)
            true
        }
    }

    override fun getItemCount() = alarms.size

    fun updateAlarms(newAlarms: List<Alarm>) {
        alarms = newAlarms
        notifyDataSetChanged()
    }
}
