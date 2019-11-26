package com.valjapan.kintai.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment(var c: Calendar, var date: Date?, var phase: String) :
    DialogFragment(), TimePickerDialog.OnTimeSetListener {

    interface OnTimeSelectedListener {
        fun onSelected(phase: String, date: Date?)
    }

    private lateinit var listener: OnTimeSelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTimeSelectedListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val sHour = String.format("%02d", hourOfDay)
        val sMinute = String.format("%02d", minute)

        val str: String =
            SimpleDateFormat(
                "EEE MM dd $sHour:$sMinute:ss z yyyy",
                Locale.JAPANESE
            ).format(date)

        Log.d("Test", str)

        val toDate: Date? =
            SimpleDateFormat("EEE MM dd HH:mm:ss z yyyy", Locale.JAPANESE).parse(str)

        listener.onSelected(phase, toDate)
    }
}