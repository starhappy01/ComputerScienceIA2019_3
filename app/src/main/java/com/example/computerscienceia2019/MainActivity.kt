package com.example.computerscienceia2019

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addEventButton = findViewById<Button>(R.id.addEventButton)
        val viewEventButton = findViewById<Button>(R.id.viewEventButton)
        val addMultEventsButton = findViewById<Button>(R.id.addMultEventsButton)

        addEventButton.setOnClickListener {
            printPrefs()
            val intent = Intent(this, addEventActivity::class.java)
            startActivity(intent);
        }

        viewEventButton.setOnClickListener {
            printPrefs()
            val intent2 = Intent(this, viewEventActivity::class.java)
            startActivity(intent2);
        }

        addMultEventsButton.setOnClickListener {
            printPrefs()
            val intent4 = Intent(this, addMultEventsActivity::class.java)
            startActivity(intent4);
        }

    }

    public fun printPrefs(): Unit {
        val TAG = "CSProj"
        val sharedPrefTime = getSharedPreferences("AlarmAppTime", Context.MODE_PRIVATE)
        val sharedPrefMesg = getSharedPreferences("AlarmAppMesg", Context.MODE_PRIVATE)
        val sharedPrefFreq = getSharedPreferences("AlarmAppFreq", Context.MODE_PRIVATE)
        val IDSharedPref = getSharedPreferences("AlarmID", Context.MODE_PRIVATE)

        // Walk through each prefs and print its key value pairs to Log.d
        val allIDs = IDSharedPref.all
        Log.d(TAG, "ID Prefs Values >>>>>>>>>>>>>>>>>>>")
        for (i in allIDs){
            Log.d(TAG, "Key = " + i.key + " Value = " + i.value)
        }
        val allTimes = sharedPrefTime.all
        Log.d(TAG, "Time Prefs Values >>>>>>>>>>>>>>>>>>>")
        for (i in allTimes){
            Log.d(TAG, "Key = " + i.key + " Value = " + i.value)
        }
        val allMesgs = sharedPrefMesg.all
        Log.d(TAG, "Mesg Prefs Values >>>>>>>>>>>>>>>>>>>")
        for (i in allMesgs){
            Log.d(TAG, "Key = " + i.key + " Value = " + i.value)
        }
        val allFreq = sharedPrefFreq.all
        Log.d(TAG, "Freq Prefs Values >>>>>>>>>>>>>>>>>>>")
        for (i in allFreq){
            Log.d(TAG, "Key = " + i.key + " Value = " + i.value)
        }
    }
}
