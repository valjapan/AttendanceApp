package com.valjapan.kintai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.valjapan.kintai.adapter.WorkData
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_work_data_detail.*
import java.text.SimpleDateFormat
import java.util.*

class WorkDataDetailActivity : AppCompatActivity() {

    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_data_detail)
        realm = Realm.getDefaultInstance()

        val dataId = intent.getStringExtra("id") ?: ""

        val data = realm?.where(WorkData::class.java)
            ?.equalTo("id", dataId)
            ?.findFirst()

        val startTime = data?.startTime
        val finishTime = data?.finishTime
        val day = SimpleDateFormat("yyyy年MM月")
        val date = SimpleDateFormat("dd", Locale.JAPANESE)
        val hour = SimpleDateFormat("HH:mm", Locale.JAPANESE)

        dateTextView.text = day.format(startTime)
        dayTextView.text = date.format(startTime)
        startTimeTextView.text = hour.format(startTime)
        if (finishTime == null) {
            finishTimeTextView.text = "出勤中！"
        } else {
            finishTimeTextView.text = hour.format(finishTime)
        }
        ssidTimeTextView.text = data?.ssid

    }
}