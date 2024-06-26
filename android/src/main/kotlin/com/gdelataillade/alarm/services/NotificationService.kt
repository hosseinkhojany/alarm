package com.gdelataillade.alarm.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.gdelataillade.alarm.services.NotificationReceiver


class NotificationHandler(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "alarm_plugin_channel"
        private const val CHANNEL_NAME = "Alarm Notification"
    }

    init {
        createNotificationChannel()
    }

   private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(id: String, title: String, body: String, fullScreen: Boolean): Notification {
//        val appIconResId = context.packageManager.getApplicationInfo(context.packageName, 0).icon
//        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: Intent()
//        val notificationPendingIntent = PendingIntent.getActivity(
//            context,
//            id.getIdFlagSecondHalfAsInt(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("id", id)
        val pendingIntent =
            PendingIntent.getBroadcast(context,
                id.getIdFlagSecondHalfAsInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val stopIntent = Intent(context, AlarmService::class.java)
        stopIntent.action = "STOP_ALARM"
        stopIntent.putExtra("id", id)

        val stopPendingIntent = PendingIntent.getService(
            context,
            id.getIdFlagSecondHalfAsInt(),
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, CHANNEL_ID) // For API 26 and above
        } else {
            Notification.Builder(context) // For lower API levels
        }

        notificationBuilder
            .setSmallIcon(R.drawable.ic_stat_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(0, "توقف", stopPendingIntent)
            .setSound(null)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

//        if (fullScreen) {
//            notificationBuilder.setFullScreenIntent(notificationPendingIntent, true)
//        }

        return notificationBuilder.build()
    }
}