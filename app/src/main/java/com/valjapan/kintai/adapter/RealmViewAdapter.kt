package com.valjapan.kintai.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.valjapan.kintai.R
import com.valjapan.kintai.WorkDataDetailActivity
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.detail_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*


class RealmViewAdapter(
    private val context: Context,
    private var objects: OrderedRealmCollection<WorkData>?,
    private val autoUpdate: Boolean
) :
    RealmRecyclerViewAdapter<WorkData, RealmViewAdapter.ViewHolder>(objects, autoUpdate) {

    var listener: AdapterView.OnItemClickListener? = null
    private var workDataActivity: WorkDataDetailActivity = WorkDataDetailActivity()

    override fun getItemCount(): Int = objects?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val works = objects?.get(position)
        val startTime = works?.startTime
        val finishTime = works?.finishTime

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

        var alertDialog: AlertDialog
        holder.cardView.setOnClickListener {
            val context: Context = context

            val view: View = LayoutInflater.from(context).inflate(R.layout.detail_dialog, null)

            val dayTextView = view.findViewById<TextView>(R.id.dayDetailTextView)
            val startDetailTimeTextView = view.findViewById<TextView>(R.id.startDetailTimeTextView)
            val finishDetailTextView = view.findViewById<TextView>(R.id.finishDetailTimeTextView)
            val ssidDetailTextView = view.findViewById<TextView>(R.id.ssidDetailTimeTextView)

            alertDialog = AlertDialog.Builder(context).setView(view).create()

            val date = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
            dayTextView.text = date.format(startTime)
            startDetailTimeTextView.text = hour.format(startTime)
            if (finishTime == null) {
                finishDetailTextView.text = "出勤中！"
            } else {
                finishDetailTextView.text = hour.format(finishTime)
            }
            ssidDetailTextView.text = works?.ssid
            alertDialog.show()

            view.doneButton.setOnClickListener {
                alertDialog.dismiss()
            }

            view.reWriteButton.setOnClickListener {
                val intent = Intent(context, WorkDataDetailActivity::class.java)
                intent.putExtra("id", works?.id)

                Log.d("Log", works?.startTime.toString())
                context.startActivity(intent)

            }

        }
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
        var cardView: CardView = view.findViewById(R.id.cardView)
    }

}

