package com.valjapan.kintai.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.valjapan.kintai.R
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*


class RealmViewAdapter(
    private val context: Context,
    private var objects: OrderedRealmCollection<WorkData>?,
    private val autoUpdate: Boolean
) :
    RealmRecyclerViewAdapter<WorkData, RealmViewAdapter.ViewHolder>(objects, autoUpdate) {

    var listener: AdapterView.OnItemClickListener? = null

    override fun getItemCount(): Int = objects?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val works = objects?.get(position)
        val startTime = works?.startTime
        val finishTime = works?.finishTime

        //Dateクラスをインスタンス
//            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE)
        val date = SimpleDateFormat("dd", Locale.JAPANESE)
        val hour = SimpleDateFormat("HH:mm", Locale.JAPANESE)

        holder.workDateText.text = date.format(startTime)
        holder.startTimeText.text = hour.format(startTime)
        if (finishTime == null) {
            holder.finishTimeText.text = "出勤中！"
        } else {
            holder.finishTimeText.text = hour.format(finishTime)
        }
        holder.ssidText.text = works?.ssid

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(context)
            .inflate(R.layout.check_work_view, viewGroup, false)

        return ViewHolder(v)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var startTimeText: TextView = view.findViewById(R.id.startTimeText)
        var finishTimeText: TextView = view.findViewById(R.id.finishTimeText)
        var workDateText: TextView = view.findViewById(R.id.dayText)
        var ssidText: TextView = view.findViewById(R.id.ssidText)
    }

    interface OnItemClickListener {
        fun onItemClick(key: String, item: WorkData)

        fun onItemDeleteClick(key: String, item: WorkData)
    }

}

