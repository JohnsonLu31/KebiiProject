package com.example.alarmManager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.R
import com.example.alarmmanager.AlarmReceiver
import com.example.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.alarm_activity.*
import java.util.*

class AlarmMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var picker: MaterialTimePicker
    lateinit var calendar: Calendar
    lateinit var alarmManager: AlarmManager
    lateinit var pendingIntent: PendingIntent

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_activity)

        createNotificationChannel()

        selectedTimeBtn.setOnClickListener {

            showTimePicker()

        }

        setAlarmBtn.setOnClickListener {

            setAlarm()
            showTime.text = "${System.currentTimeMillis()} , ${calendar.timeInMillis}"

        }

        cancelAlarmBtn.setOnClickListener {

            cancelAlarm()

        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm() {

        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this@AlarmMain, 1, intent, 0)

        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarm() {

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(this@AlarmMain, 1, intent, 0)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Alarm set Sucessfully", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(Date().hours)
            .setMinute(Date().minutes)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "android")

        picker.addOnPositiveButtonClickListener {

            if (picker.hour > 12) {
                selectedTime.text = String.format("%02d", picker.hour-12)+" : "+ String.format("%02d", picker.minute)+" PM "
            } else {
                selectedTime.text = "${picker.hour} : ${picker.minute} AM"
            }

            calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
            calendar.set(Calendar.MINUTE, picker.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "ReminderChannel"
            val description = "Channel for alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("android", name, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}