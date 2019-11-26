package com.valjapan.kintai.activity

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.valjapan.kintai.R
import com.valjapan.kintai.adapter.WorkData
import com.valjapan.kintai.fragment.FinishActivityListener
import com.valjapan.kintai.fragment.TimePickerFragment
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_work_data_detail.*
import java.text.SimpleDateFormat
import java.util.*


class WorkDataDetailActivity : FragmentActivity(), TimePickerFragment.OnTimeSelectedListener {

    private var realm: Realm? = null
    private var startDate: Date? = null
    private var finishDate: Date? = null

    private var listener: FinishActivityListener? = null


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
        if (finishDate == null) {
            finishTimeTextView.text = "出勤中！"
        } else {
            finishTimeTextView.text = hour.format(finishDate)
            finishTimeTextView.setOnClickListener {
                val cal: Calendar = Calendar.getInstance()
                cal.time = startDate
                val timeFragment: DialogFragment = TimePickerFragment(cal, finishDate, finishPhase)
                timeFragment.show(supportFragmentManager, "timePicker")
            }
        }
        ssidTimeTextView.text = data?.ssid

        startTimeTextView.setOnClickListener {
            val cal: Calendar = Calendar.getInstance()
            cal.time = startDate
            val timeFragment: DialogFragment = TimePickerFragment(cal, startDate, startPhase)
            timeFragment.show(supportFragmentManager, "timePicker")
        }

        reWriteButton.setOnClickListener {
            realm?.executeTransaction {
                data?.startTime = startDate
                data?.finishTime = finishDate
            }
            listener?.updateRecyclerView()
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

    fun inject(listener: FinishActivityListener) {
        this.listener = listener
    }

    companion object {
        const val startPhase = "START_PHASE"
        const val finishPhase = "FINISH_PHASE"
    }
}