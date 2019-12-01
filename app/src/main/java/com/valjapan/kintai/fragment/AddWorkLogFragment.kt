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
        val alarmManager: AlarmManager = context?.getSystemService(ALARM_SERVICE) as AlarmManager
        val resultIntent = Intent(context, TaskAlarmReceiver::class.java)
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
                val resultPendingIntent = PendingIntent.getBroadcast(
                    context,
                    identifier,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                resultPendingIntent.cancel()
                alarmManager.cancel(resultPendingIntent)

            } else {
                view.addWorkDataImage.setImageResource(R.drawable.shape_round_blue)
                view.kakunin.text = "TAIKIN"
                addRealm(getWiFi())

                resultIntent.putExtra(MainActivity.EXTRA_TASK, identifier)
                //TAIKIN画面になった時(
                val resultPendingIntent = PendingIntent.getBroadcast(
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

                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    timeCalender.timeInMillis,
                    resultPendingIntent
                )
            }
        }
        return view
    }

    private fun getWiFi(): String {
        val manager = context?.getSystemService(WIFI_SERVICE) as WifiManager?
        val info = manager!!.connectionInfo
        val ssid = String.format("SSID : %s", info.ssid)
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
