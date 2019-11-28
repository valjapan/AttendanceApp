package com.valjapan.kintai.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.valjapan.kintai.R
import com.valjapan.kintai.activity.MainActivity
import com.valjapan.kintai.adapter.TaskAlarmReceiver
import com.valjapan.kintai.adapter.WorkData
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_shukkin.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddWorkLogFragment : Fragment() {
    private var realm: Realm? = null
    private lateinit var workData: WorkData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AddWorkLogFragment", "Fragmentの初回起動")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("AddWorkLogFragment", "ShukkinFragmentをCreateViewしました")
        realm = Realm.getDefaultInstance()
        val view = inflater.inflate(R.layout.fragment_shukkin, container, false)
        workData = WorkData()

        val identifier = if (realm?.where(WorkData::class.java)?.findAll()?.size != null) {
            realm!!.where(WorkData::class.java)!!.findAll()!!.size
        } else {
            0
        }

        if (readRealm() != null) {
            view.addWorkDataImage.setImageResource(R.drawable.shape_round_blue)
            view.kakunin.text = "TAIKIN"
        } else {
            view.addWorkDataImage.setImageResource(R.drawable.shape_round)
            view.kakunin.text = "SHUKKIN"
        }

        view.addWorkDataImage.findViewById<ImageView>(R.id.addWorkDataImage).setOnClickListener {
            val data: WorkData? = readRealm()
            if (data != null) {
                view.addWorkDataImage.setImageResource(R.drawable.shape_round)
                view.kakunin.text = "SHUKKIN"
                reWriteRealm(data)
            } else {
                view.addWorkDataImage.setImageResource(R.drawable.shape_round_blue)
                view.kakunin.text = "TAIKIN"
                addRealm(getWiFi())

                //TAIKIN画面になった時(
                val resultIntent = Intent(context, TaskAlarmReceiver::class.java)
                resultIntent.putExtra(MainActivity.EXTRA_TASK, identifier)
                val resultPendeingIntent = PendingIntent.getBroadcast(
                    context,
                    identifier,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val calender = Calendar.getInstance()
                val year = calender.get(Calendar.YEAR)
                val month = calender.get(Calendar.MONTH)
                val day = calender.get(Calendar.DATE) + 1 //次の日
                val timeCalender = GregorianCalendar(year, month, day, 0, 0)

                val alarmManager: AlarmManager =
                    context?.getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    timeCalender.timeInMillis,
                    resultPendeingIntent
                )
                Toast.makeText(
                    context,
                    "alarm start", Toast.LENGTH_SHORT
                ).show()

            }
        }
        return view
    }

    private fun getWiFi(): String {
        val manager = context?.getSystemService(WIFI_SERVICE) as WifiManager?
        val info = manager!!.connectionInfo
        val ssid = String.format("SSID : %s", info.ssid)
        val ipAdr = info.ipAddress
        val ip = String.format(
            "IP Adrress : %02d.%02d.%02d.%02d",
            ipAdr shr 0 and 0xff,
            ipAdr shr 8 and 0xff,
            ipAdr shr 16 and 0xff,
            ipAdr shr 24 and 0xff
        )
        val mac = String.format("MAC Address : %s", info.macAddress)
        val rssi: Int = info.rssi
        val level = WifiManager.calculateSignalLevel(rssi, 5)
        val rssiString: String = String.format("RSSI : %d / Level : %d/4", rssi, level)

        Log.d("ssid", ssid)
        Log.d("mac", mac)
        Log.d("ip", ip)
        Log.d("rssi", rssiString)

        return ssid
    }

    private fun addRealm(ssid: String) {
        realm!!.executeTransaction {
            workData = it.createObject(
                WorkData::class.java,
                UUID.randomUUID().toString()
            ) // PrimaryKeyとなるプロパティの値を入れる
            workData.year = nowYear()
            workData.month = nowMonth()
            workData.startTime = nowDate()
            workData.finishTime = null
            workData.ssid = ssid
        }
        realm!!.refresh()
    }

    private fun reWriteRealm(workData: WorkData?) {
        realm!!.executeTransaction {
            workData!!.finishTime = nowDate()
        }
        Log.d("reWriteRealm", workData.toString())
    }

    private fun readRealm(): WorkData? {
        val data = realm?.where(WorkData::class.java)?.isNull("finishTime")?.findFirst()
        Log.d("readRealm", data.toString())
        return data
    }

    private fun nowYear(): Int {
        val date = Date()
        val formatDate = SimpleDateFormat("yyyy", Locale.JAPANESE)
        return Integer.parseInt(formatDate.format(date))
    }

    private fun nowMonth(): Int {
        val date = Date()
        val formatDate = SimpleDateFormat("MM", Locale.JAPANESE)
        return Integer.parseInt(formatDate.format(date))
    }

    private fun nowDate(): Date {
        //現在時間を取得
        return Date()
    }
}
