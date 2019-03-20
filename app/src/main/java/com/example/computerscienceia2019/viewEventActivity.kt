package com.example.computerscienceia2019

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.util.ArraySet
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_view_event.*
import kotlinx.android.synthetic.main.show_events_layout.view.*
import java.util.*

class viewEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cleanUp()
        val sharedPrefTime = getSharedPreferences("AlarmAppTime", Context.MODE_PRIVATE)
        val sharedPrefMesg = getSharedPreferences("AlarmAppMesg", Context.MODE_PRIVATE)
        val sharedPrefFreq = getSharedPreferences("AlarmAppFreq", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_view_event)

        var cal = Calendar.getInstance()

        val allMesgs = sharedPrefMesg.all
        val sizeMesgs = allMesgs.size
        val events = arrayOfNulls<String>(sizeMesgs)
        var indx = 0

        for (i in allMesgs){
            // Extract messages and add to array
            var key = i.key
            var alarmTime = sharedPrefTime.getString(key, "").toLong()
            cal.setTimeInMillis(alarmTime)
            var frequency = sharedPrefFreq.getLong(key, 0)
            var freqString = " "
            if(frequency == AlarmManager.INTERVAL_DAY){
                freqString = "   -- DAILY"
            }
            else if(frequency == AlarmManager.INTERVAL_DAY * 7){
                freqString = "   -- WEEKLY"
            }
            else if(frequency == AlarmManager.INTERVAL_DAY * 30){
                freqString = "   -- MONTHLY"
            }
            var value = "\n  " + key + ": " + i.value.toString() + "\n " + cal.time + freqString + "\n"
            events.set(indx, value)
            indx++
        }


        var listView : ListView= findViewById(R.id.events_list_view)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, events)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // This is your listview's selected item
            val listItem : String = parent.getItemAtPosition(position).toString()

            var strList = listItem.split(":")
            var key = strList[0].subSequence(3, strList[0].length)
            Toast.makeText(this, "Deleting event!", Toast.LENGTH_LONG).show()

            var editorTime = sharedPrefTime.edit()
            editorTime.remove(key.toString())
            editorTime.commit()

            var editorMesg = sharedPrefMesg.edit()
            editorMesg.remove(key.toString())
            editorMesg.commit()

            var editorFreq = sharedPrefFreq.edit()
            editorFreq.remove(key.toString())
            editorFreq.commit()


            val intent5 = Intent(this, MainActivity::class.java)
            startActivity(intent5);

            val alarmMan = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastIntent = Intent(this, AlarmBroadcastReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(this, key.toString().toInt(),broadcastIntent,0)
            alarmMan.cancel(
                pIntent
            )



        }

    }

    public fun cleanUp(){
        val sharedPrefTime = getSharedPreferences("AlarmAppTime", Context.MODE_PRIVATE)
        val sharedPrefMesg = getSharedPreferences("AlarmAppMesg", Context.MODE_PRIVATE)
        val sharedPrefFreq = getSharedPreferences("AlarmAppFreq", Context.MODE_PRIVATE)
        var cal = Calendar.getInstance()
        val allTimes = sharedPrefTime.all
        val allFreq = sharedPrefFreq.all
        for (i in allTimes){
            val time: Long = i.value.toString().toLong()
            // Check if the time of alarm is already past.  If so, delete it if not repeating
            if(cal.timeInMillis > time){
                val key = i.key
                //checking if key is present in sharedPrefFreq.  If not, delete the prefs since it is not repeating
                var freq = sharedPrefFreq.getLong(key, -1)
                //Log.d("CSProj", " Freq value for " + key + " is : " + freq)
                if(freq < 0) {
                    var editorTime = sharedPrefTime.edit()
                    editorTime.remove(key)
                    editorTime.commit()


                    var editorMesg = sharedPrefMesg.edit()
                    editorMesg.remove(key)
                    editorMesg.commit()

                    var editorFreq = sharedPrefFreq.edit()
                    editorFreq.remove(key)
                    editorFreq.commit()

                    //Log.d("CSProj", "Deleting " + i.key + " and " + i.value)
                }
            }

        }
    }


}



