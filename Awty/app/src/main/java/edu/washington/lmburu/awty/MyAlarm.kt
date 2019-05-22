package edu.washington.lmburu.awty


import android.app.AlarmManager
import android.app.PendingIntent.getBroadcast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
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


    override fun onReceive(context: Context, intent: Intent?) {

        try{
            sendSMS(context, intent)
        } catch ( e: Exception){
            e.printStackTrace()
        }

            if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
                // Set the alarm here.
                val data = context?.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                val storedInterval = data!!.getLong(INTERVAL, 0)
                alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pendingIntent = getBroadcast(context, 0, intent, 0)
                alarmMgr?.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + storedInterval,
                    storedInterval,
                    pendingIntent
                )
//            Log.i(TAG, "reboot")
            }

        }

        fun sendSMS(context: Context, intent: Intent?){

            val smsManager = SmsManager.getDefault()
            val givenPhoneNum = intent?.getStringExtra(PHONE_NUM)
            val givenMsg = intent?.getStringExtra(MSG)
            Log.i(TAG, "Given phone# is $givenPhoneNum and Given msg is $givenMsg")
            Toast.makeText(context, "$givenPhoneNum: Are We There Yet?", Toast.LENGTH_SHORT).show()
            val number = "$givenPhoneNum"
            val message = "$givenMsg"
            smsManager.sendTextMessage(number, null, message, null, null)
        }

}


