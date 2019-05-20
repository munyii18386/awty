package edu.washington.lmburu.awty

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent.getBroadcast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast


private const val TAG = "alarm"
class MyAlarm: BroadcastReceiver() {
    companion object{
        private const val PREF = "pref"
        private const val MSG ="msg"
        private const val PHONE_NUM = "phone"
        const val INTERVAL = "interval"
        private var alarmMgr: AlarmManager? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val givenPhoneNum = intent?.getStringExtra("phone")
        Toast.makeText(context, "$givenPhoneNum: Are We There Yet?", Toast.LENGTH_SHORT).show()
//        Log.i(TAG, "next round")

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            val data = context?.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            val storedInterval = data!!.getLong(INTERVAL, 0)
            alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = getBroadcast(context, 0, intent, 0)
            alarmMgr?.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ storedInterval, storedInterval, pendingIntent)
//            Log.i(TAG, "reboot")
        }


    }
}


