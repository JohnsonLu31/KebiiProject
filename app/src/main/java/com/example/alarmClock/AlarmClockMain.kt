package com.example.alarmClock


import android.app.Activity
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*
import com.example.R
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.alarm_main.*

class AlarmClockMain : AppCompatActivity() {

    lateinit var ring: Ringtone


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_main)


        ring = RingtoneManager.getRingtone(
            this,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        )

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (tv_time.text == AlarmTime() && sw_switch.isChecked) {
                    ring.play()
                    Log.d("Test", "Ring Play")
                } else
                    ring.stop()
            }

        }, 0, 1000)
    }


    override fun onDestroy() {
        super.onDestroy()
        sw_switch.isChecked = false
        Log.d("Test", "Successfully destroy")
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
                stringAlarmTime = "下午$alarmHours:$stringAlarmMinutes"
            else stringAlarmTime = "下午$alarmHours:$stringAlarmMinutes"
        } else {
            stringAlarmTime = "上午$alarmHours:$stringAlarmMinutes"
        }
        return stringAlarmTime
    }
}

//https://www.youtube.com/watch?v=vJOW_Idnx7w