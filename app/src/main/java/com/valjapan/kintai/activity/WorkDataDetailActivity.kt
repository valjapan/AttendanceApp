package com.valjapan.kintai.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.valjapan.kintai.R
import com.valjapan.kintai.adapter.WorkData
import com.valjapan.kintai.fragment.TimePickerFragment
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_work_data_detail.*
import java.text.SimpleDateFormat
import java.util.*

class WorkDataDetailActivity : FragmentActivity(), TimePickerFragment.OnTimeSelectedListener {
    private var realm: Realm? = null
    private var startDate: Date? = null
    private var finishDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_data_detail)
        realm = Realm.getDefaultInstance()

        val dataId = intent.getStringExtra("id") ?: ""

        val data = realm?.where(WorkData::class.java)
            ?.equalTo("id", dataId)
            ?.findFirst()

        startDate = data?.startTime
        finishDate = data?.finishTime
        val day = SimpleDateFormat("yyyy年MM月")
        val date = SimpleDateFormat("dd", Locale.JAPANESE)
        val hour = SimpleDateFormat("HH:mm", Locale.JAPANESE)

        dateTextView.text = day.format(startDate)
        dayTextView.text = date.format(startDate)
        startTimeTextView.text = hour.format(startDate)
        when (finishDate) {
            null -> finishTimeTextView.text = "出勤中！"
            else -> finishTimeTextView.text = hour.format(finishDate)
        }
        finishTimeTextView.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            when (finishDate) {
                null -> {
                    cal.time = Date()
                    finishDate = Date()
                }
                else -> cal.time = finishDate
            }
            val timeFragment: DialogFragment = TimePickerFragment(cal, finishDate, finishPhase)
            timeFragment.show(supportFragmentManager, "timePicker")
        }
        ssidTimeTextView.text = data?.ssid

        startTimeTextView.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            cal.time = startDate
            val timeFragment: DialogFragment = TimePickerFragment(cal, startDate, startPhase)
            timeFragment.show(supportFragmentManager, "timePicker")
        }

        reWriteButton.setOnClickListener {
            if (finishDate == null) finish()
            else if ((finishDate!!.time - startDate!!.time) < 0) {
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("編集エラー")
                    .setMessage("開始時間と終了時間をもう一度確認してください！")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }
            realm?.executeTransaction {
                data?.startTime = startDate
                data?.finishTime = finishDate
            }
            finish()
        }
    }

    override fun onSelected(phase: String, date: Date?) {
        val hour = SimpleDateFormat("HH:mm", Locale.JAPANESE)
        when (phase) {
            startPhase -> {
                startTimeTextView.text = hour.format(date)
                startDate = date
            }
            finishPhase -> {
                finishTimeTextView.text = hour.format(date)
                finishDate = date
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
    }

    companion object {
        const val startPhase = "START_PHASE"
        const val finishPhase = "FINISH_PHASE"
    }
}