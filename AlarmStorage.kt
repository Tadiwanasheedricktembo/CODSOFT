package com.querycubix.alarmclock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AlarmStorage {
    private const val PREFS_NAME = "alarm_prefs"
    private const val ALARMS_KEY = "alarms_list"

    fun getAlarms(context: Context): MutableList<Alarm> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(ALARMS_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Alarm>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveAlarms(context: Context, alarms: List<Alarm>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(alarms)
        prefs.edit().putString(ALARMS_KEY, json).apply()
    }

    fun addAlarm(context: Context, alarm: Alarm) {
        val alarms = getAlarms(context)
        alarms.add(alarm)
        saveAlarms(context, alarms)
    }

    fun updateAlarm(context: Context, updatedAlarm: Alarm) {
        val alarms = getAlarms(context)
        val index = alarms.indexOfFirst { it.id == updatedAlarm.id }
        if (index != -1) {
            alarms[index] = updatedAlarm
            saveAlarms(context, alarms)
        }
    }
}
