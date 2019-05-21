package edu.washington.lmburu.awty

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.icu.util.DateInterval
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "main"

class MainActivity : AppCompatActivity() {

    companion object{
        private lateinit var sharedPreferences: SharedPreferences
        private var alarmMgr: AlarmManager? = null
        private lateinit var alarmIntent: PendingIntent
        private  var givenMsg = ""
        private  var givenPhoneNum: Editable? = null
        private var givenInterval = 0
        private const val PREF = "pref"
        private const val MSG ="msg"
        private const val PHONE_NUM = "phone"
        private const val INTERVAL = "interval"
        val REQUEST_SMS_SEND_PERMISSION = 1234
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(PREF, Context.MODE_PRIVATE)
//      get the Alarm Manager
        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        tv_btn.isEnabled = false
        tv_phone.isEnabled = false
        tv_interval.isEnabled = false

        tv_msg.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                tv_phone.isEnabled = !tv_msg.text.isBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        tv_phone.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                tv_interval.isEnabled = !tv_phone.text.isBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        tv_interval.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(!tv_interval.text.toString().startsWith("0")){
                    tv_btn.isEnabled = !tv_interval.text.isBlank()
                }else{
                    Toast.makeText(this@MainActivity, "Interval has to be greater than zero", Toast.LENGTH_LONG).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        tv_btn.setOnClickListener {
            givenMsg = tv_msg.text.toString()
            givenPhoneNum = tv_phone.text
            givenInterval = tv_interval.text.toString().toInt()
            val interval:Long = givenInterval.toLong() * 1000 * 60
            val currentTime = System.currentTimeMillis()
            sharedPreferences
                .edit()
                .putString(MSG, givenMsg)
                .putLong(INTERVAL, interval)
                .putString(PHONE_NUM, givenPhoneNum.toString())
                .apply()

//            create pending intent
            alarmIntent = Intent(this, MyAlarm::class.java)
                .putExtra(MSG, givenMsg)
                .putExtra(PHONE_NUM, givenPhoneNum.toString())
                .let { intent ->
                    PendingIntent.getBroadcast(this, 0, intent, 0)
                }
            if(tv_btn.text == "START"){

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                    // Need to request SEND_SMS permission
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.SEND_SMS),
                        REQUEST_SMS_SEND_PERMISSION)

                } else {

                    // Has Permissions, Send away!
                    alarmMgr?.setRepeating(AlarmManager.RTC_WAKEUP, currentTime+ interval, interval, alarmIntent)
                    Toast.makeText(this, "Alarm has been set", Toast.LENGTH_SHORT).show()

                }

                tv_btn.text = "STOP"
            } else{
                alarmMgr?.cancel(alarmIntent)
                Toast.makeText(this, "Alarm has been stopped", Toast.LENGTH_SHORT).show()
                tv_btn.text = "START"
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        givenInterval = tv_interval.text.toString().toInt()
        val interval:Long = givenInterval.toLong() * 1000 * 60
        val currentTime = System.currentTimeMillis()

        when (requestCode) {
            REQUEST_SMS_SEND_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted. proceed with need to do.
                    Toast.makeText(this, "Approved", Toast.LENGTH_SHORT).show()
                    alarmMgr?.setRepeating(AlarmManager.RTC_WAKEUP, currentTime+ interval, interval, alarmIntent)

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }


        }
    }


}

