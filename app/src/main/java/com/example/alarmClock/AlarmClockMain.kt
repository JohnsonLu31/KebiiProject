package com.example.alarmClock

import android.app.Service
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.example.R
import kotlinx.android.synthetic.main.alarm_main.*

class AlarmClockMain : AppCompatActivity() {

    lateinit var ring: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_main)

        ring = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (tv_time.text == AlarmTime())
                    ring.play()
                else
                    ring.stop()
                }

        }, 0, 1000)
    }

    override fun onStop() {
        ring.stop()
        super.onStop()
    }


    fun AlarmTime(): String {

        var alarmHours = tp_timepicker.currentHour
        val alarmMinutes = tp_timepicker.currentMinute

        val stringAlarmMinutes: String

        if (alarmMinutes < 10)
            stringAlarmMinutes = "0$alarmMinutes"
        else
            stringAlarmMinutes = "$alarmMinutes"

        val stringAlarmTime: String

        if (alarmHours > 12) {
            alarmHours -= 12
            if (alarmHours < 10)
            stringAlarmTime = "0$alarmHours:$stringAlarmMinutes"
            else stringAlarmTime = "$alarmHours:$stringAlarmMinutes"
        } else {
            stringAlarmTime = "0$alarmHours:$stringAlarmMinutes"
        }
        return stringAlarmTime
    }
}

//https://www.youtube.com/watch?v=vJOW_Idnx7w