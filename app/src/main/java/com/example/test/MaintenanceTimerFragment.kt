package com.example.test

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import java.util.*


class MaintenanceTimerFragment : Fragment(), View.OnClickListener {
    var alarmTimePicker: TimePicker? = null
    var pendingIntent: PendingIntent? = null
    var alarmManager: AlarmManager? = null
    var toggleButton:ToggleButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maintenance_timer, container, false)
        alarmTimePicker = view.findViewById(R.id.timePicker);
        toggleButton = view.findViewById(R.id.toggleButton)
        alarmManager =  requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
        toggleButton?.setOnClickListener{
            this
        }

        // Inflate the layout for this fragment
        return view;
    }

/*
    // OnToggleClicked() method is implemented the time functionality
    @SuppressLint("ShortAlarm")
    fun onToggleClicked(view:View) {
        Toast.makeText(activity, "I have been clicked", Toast.LENGTH_SHORT).show()
        var time: Long
        if ((view as ToggleButton).isChecked) {
            Toast.makeText(activity, "ALARM ON", Toast.LENGTH_SHORT).show()
            val calendar: Calendar = Calendar.getInstance()

            // calendar is called to get current time in hour and minute
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker!!.currentHour)
            calendar.set(Calendar.MINUTE, alarmTimePicker!!.currentMinute)

            // using intent i have class AlarmReceiver class which inherits
            // BroadcastReceiver
            val intent = Intent(activity, AlarmReceiver::class.java)

            // we call broadcast using pendingIntent
            pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0)
            time = calendar.getTimeInMillis() - calendar.getTimeInMillis() % 60000
            if (System.currentTimeMillis() > time) {
                // setting time as AM and PM
                time =
                    if (Calendar.AM_PM === 0) time + 1000 * 60 * 60 * 12 else time + 1000 * 60 * 60 * 24
            }
            // Alarm rings continuously until toggle button is turned off
            alarmManager!!.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000)
            alarmManager!!.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (time * 1000), pendingIntent);
        } else {
            pendingIntent?.let { alarmManager!!.cancel(it) }
            Toast.makeText(activity, "ALARM OFF", Toast.LENGTH_SHORT).show()
        }
    }
*/
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.toggleButton ->{
                //onToggleClicked(view)
            }
        }




    }




}