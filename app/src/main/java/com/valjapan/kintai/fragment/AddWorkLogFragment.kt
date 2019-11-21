package com.valjapan.kintai.fragment

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.valjapan.kintai.R
import com.valjapan.kintai.adapter.WorkData
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_shukkin.view.*
import java.util.*


class AddWorkLogFragment : Fragment() {
    private var realm: Realm? = null
    lateinit var workData: WorkData

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

        if (readRealm() != null) {
            view.addWorkDataImage.setImageResource(R.drawable.shape_round_orange)
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
                view.addWorkDataImage.setImageResource(R.drawable.shape_round_orange)
                view.kakunin.text = "TAIKIN"
                addRealm(getWiFi())
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
            workData = it.createObject(WorkData::class.java, UUID.randomUUID().toString()) // PrimaryKeyとなるプロパティの値を入れる
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

    private fun nowDate(): Date {
        //現在時間を取得
        return Date()
    }
}
