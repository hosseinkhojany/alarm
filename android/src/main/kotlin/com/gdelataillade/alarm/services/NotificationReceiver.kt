package com.gdelataillade.alarm.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.gdelataillade.alarm.alarm.setClickedAlarmPrefs


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Save value to SharedPreferences
        context.setClickedAlarmPrefs(intent.getStringExtra("id"))
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: Intent()
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

