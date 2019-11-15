package com.valjapan.kintai.adapter

import io.realm.RealmObject
import java.util.*

open class WorkData : RealmObject() {
    var startTime: Date? = null
    var finishTime: Date? = null
    var ssid: String? = null

    fun WorkData() {
    }

    fun WorkData(
        startTime: Date,
        finishTime: Date,
        ssid: String
    ) {
        this.startTime = startTime
        this.finishTime = finishTime
        this.ssid = ssid
    }
}