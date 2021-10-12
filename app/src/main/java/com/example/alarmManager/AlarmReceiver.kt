package com.example.alarmmanager

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.R
import com.example.alarmManager.AlarmMain

class AlarmReceiver : BroadcastReceiver() {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {

        val i = Intent(context, AlarmMain::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)

        val builder = NotificationCompat.Builder(context, "android")
            builder.setSmallIcon(R.drawable.ic_launcher_background)
            builder.setContentTitle("Alarm Manager")
            builder.setContentText("Android Alarm clock")
            builder.setAutoCancel(true)
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setContentIntent(pendingIntent)

    val notificationCompat = NotificationManagerCompat.from(context)
    notificationCompat.notify(123, builder.build())
}
}