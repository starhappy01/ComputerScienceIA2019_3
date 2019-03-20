package com.example.computerscienceia2019

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class addEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        //variables whoo//
        val TAG = "CSProj"
        val reminder = findViewById<EditText>(R.id.reminder)
        val addDateButton = findViewById<Button>(R.id.addDateButton)
        val addEventOKButton = findViewById<Button>(R.id.AddEventOKButton)
        val showDate : TextView = findViewById(R.id.showDate)
        val remText : TextView = findViewById(R.id.remText)
        val addTimeButton = findViewById<Button>(R.id.addTimeButton)
        val showTime : TextView = findViewById(R.id.showTime)
        var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        var cal = Calendar.getInstance()

        val remId = "NextEventID"
        val IDSharedPref = getSharedPreferences("AlarmID", Context.MODE_PRIVATE)

        val sharedPrefTime = getSharedPreferences("AlarmAppTime", Context.MODE_PRIVATE)

        val sharedPrefMesg = getSharedPreferences("AlarmAppMesg", Context.MODE_PRIVATE)
        var reqId = IDSharedPref.getInt(remId, 1)

        val sharedPrefFreq = getSharedPreferences("AlarmAppFreq", Context.MODE_PRIVATE)

        //clear sharedpreferences for testing
        // *****************************************
//        var editor = sharedPrefTime.edit()
//        editor.clear()
//        editor.commit()
//
//        editor = sharedPrefMesg.edit()
//        editor.clear()
//        editor.commit()
//
//        editor = sharedPrefFreq.edit()
//        editor.clear()
//        editor.commit()
//
//        editor = IDSharedPref.edit()
//        editor.clear()
//        editor.commit()

        //******************************************

        //replaces dashes with the selected date//
        fun updateDateInView() {
            val myFormat = "MM/dd/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            showDate!!.text = sdf.format(cal.getTime())
        }


        //sets up datepicker//
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        //once addDate button clicked, opens datepicker//
        addDateButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@addEventActivity, dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })


        //replaces dashes with the selected time//
        fun updateTimeInView(){
            val myFormat = "hh:mm a"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            //Toast.makeText(this, timeFormat.format(cal.getTime()), Toast.LENGTH_SHORT).show()
            showTime!!.text = sdf.format(cal.getTime())

        }


        //once addTime button clicked, opens timepicker//
        addTimeButton.setOnClickListener {
            var timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                updateTimeInView()
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        //once addEvent button clicked, sets alarm//
        addEventOKButton.setOnClickListener {
            val alarmMan = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastIntent = Intent(this, AlarmBroadcastReceiver::class.java)
            broadcastIntent.putExtra("Text", reminder.text.toString())

            val pIntent = PendingIntent.getBroadcast(this, reqId,broadcastIntent,0)
            //Log.d(TAG, "Id is " + reqId + " and reminder text is = " + reminder.text.toString())
            var editorTime = sharedPrefTime.edit()
            //val remSet : Set<String> = setOf(reminder.text.toString(), cal.timeInMillis.toString())
            editorTime.putString(reqId.toString(), cal.timeInMillis.toString())

            editorTime.commit()

            var editorMesg = sharedPrefMesg.edit()
            editorMesg.putString(reqId.toString(), reminder.text.toString())
            editorMesg.commit()

            reqId++
            // save reqID to IDsharedpreferences
            var IDEditor = IDSharedPref.edit()
            IDEditor.putInt(remId, reqId)

            IDEditor.commit()

            alarmMan.set(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pIntent
            )

            //Log.d(TAG, "Alarm set for: " + cal.time)
            val mesg : Toast = Toast.makeText(this, "Your alarm is set for " + cal.time,  Toast.LENGTH_LONG)
            mesg.show()
            // Store event details in data store.  Will be needed for editing it or deleting it

            val intent3 = Intent(this, MainActivity::class.java)
            startActivity(intent3);
        }
    }
}

class AlarmBroadcastReceiver : BroadcastReceiver() {

    var TAG = "CSProj"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        var cal = Calendar.getInstance()

        //Toast.makeText(context, "Fired alarm.  Yay!!!",  Toast.LENGTH_LONG).show()
        //Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>> ALARM FIRED")

        val reminderText = intent?.getStringExtra("Text")
        //Log.d(TAG, "Extracted text = " + reminderText)
        // Create the notification to be shown
        val mBuilder = NotificationCompat.Builder(context!!, "ONE")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(" Alarm App ")
            .setContentText(reminderText)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val ch : NotificationChannel = NotificationChannel("ONE", "Ch ONE", NotificationManager.IMPORTANCE_DEFAULT).apply{
            description = "Channel ONE"
        }
        // Get the Notification manager service
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(ch)
        // Show a notification
        nm.notify(cal.get(Calendar.MINUTE), mBuilder.build())
    }

}
