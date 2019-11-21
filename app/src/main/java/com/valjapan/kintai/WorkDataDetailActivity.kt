package com.valjapan.kintai

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.valjapan.kintai.adapter.WorkData
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_work_data_detail.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WorkDataDetailActivity : AppCompatActivity() {

    private var realm: Realm? = null
    private var dateFormat: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_data_detail)
        realm = Realm.getDefaultInstance()

        val dataId = intent.getStringExtra("id") ?: ""
        Log.d("dataId", dataId)


        val data = realm?.where(WorkData::class.java)
            ?.equalTo("id", dataId)
            ?.findFirst()

        val startTime = data?.startTime
        val finishTime = data?.finishTime
        val date = SimpleDateFormat("dd", Locale.JAPANESE)
        val hour = SimpleDateFormat("HH:mm", Locale.JAPANESE)

        dayTextView.text = date.format(startTime)
        startTimeTextView.text = hour.format(startTime)
        finishTimeTextView.text = hour.format(finishTime)
        ssidTimeTextView.text = data?.ssid
    }
}
