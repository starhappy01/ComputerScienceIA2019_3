package com.example.computerscienceia2019

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class addMultEventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mult_events)

        val mRemText = findViewById<TextView>(R.id.mRemText)
        val mReminder : EditText = findViewById(R.id.mReminder)
        val mAddDateButton = findViewById<Button>(R.id.mAddDateButton)
        val mAddTimeButton = findViewById<Button>(R.id.mAddTimeButton)
        val mShowDate = findViewById<TextView>(R.id.mShowDate)
        val mShowTime = findViewById<TextView>(R.id.mShowTime)
        val freqText = findViewById<TextView>(R.id.freqText)
        val radGroup : RadioGroup = findViewById(R.id.radGroup)
        val dailyButton : RadioButton = findViewById(R.id.dailyButton)
        val weeklyButton : RadioButton = findViewById(R.id.weeklyButton)
        val monthlyButton : RadioButton = findViewById(R.id.monthlyButton)
        val mAddEventButton = findViewById<Button>(R.id.mAddEventButton)

        val IDSharedPref = getSharedPreferences("AlarmID", Context.MODE_PRIVATE)
        val sharedPrefTime = getSharedPreferences("AlarmAppTime", Context.MODE_PRIVATE)

        val sharedPrefMesg = getSharedPreferences("AlarmAppMesg", Context.MODE_PRIVATE)
        var reqId = IDSharedPref.getInt("NextEventID", 1)

        val sharedPrefFreq = getSharedPreferences("AlarmAppFreq", Context.MODE_PRIVATE)
        var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        var cal = Calendar.getInstance()

        fun updateDateInActivity() {
            val myFormat = "MM/dd/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            mShowDate!!.text = sdf.format(cal.getTime())
        }

        //sets up datepicker//
        val mDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInActivity()
            }
        }

        //once addDate button clicked, opens datepicker//
        mAddDateButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@addMultEventsActivity, mDateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })


        //replaces dashes with the selected time//
        fun updateTimeInActivity(){
            val myFormat = "hh:mm a"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            //Toast.makeText(this, timeFormat.format(cal.getTime()), Toast.LENGTH_SHORT).show()
            mShowTime!!.text = sdf.format(cal.getTime())

        }


        //once addTime button clicked, opens timepicker//
        mAddTimeButton.setOnClickListener {
            var mTimeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                updateTimeInActivity()
            }
            TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        mAddEventButton.setOnClickListener {
            val mAlarmMan = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastIntent = Intent(this, AlarmBroadcastReceiver::class.java)
            broadcastIntent.putExtra("Text", mReminder.text.toString())

            val pIntent = PendingIntent.getBroadcast(this, reqId,broadcastIntent,0)
            Log.d("CSProj", "Id is " + reqId + " and reminder text is = " + mReminder.text.toString())

            val radButtonId = radGroup.getCheckedRadioButtonId()
            val selectedRadBut : RadioButton = findViewById(radButtonId)

            var interval = AlarmManager.INTERVAL_DAY

            if(selectedRadBut.equals(weeklyButton)){
                interval = interval * 7
            }
            else if(selectedRadBut.equals(monthlyButton)){
                interval = interval * 30
            }

            mAlarmMan?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                interval,
                pIntent
            )

            var editorTime = sharedPrefTime.edit()
            //val remSet : Set<String> = setOf(reminder.text.toString(), cal.timeInMillis.toString())
            editorTime.putString(reqId.toString(), cal.timeInMillis.toString())

            editorTime.commit()


            var editorMesg = sharedPrefMesg.edit()
            editorMesg.putString(reqId.toString(), mReminder.text.toString())
            editorMesg.commit()

            var editorFreq = sharedPrefFreq.edit()
            editorFreq.putLong(reqId.toString(), interval)
            editorFreq.commit()

            reqId++

            var IDEditor = IDSharedPref.edit()
            IDEditor.putInt("NextEventID", reqId)

            IDEditor.commit()


            val intent5 = Intent(this, MainActivity::class.java)
            startActivity(intent5);
        }




    }
}
