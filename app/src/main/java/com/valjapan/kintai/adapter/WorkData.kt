package com.valjapan.kintai.adapter

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class WorkData : RealmObject() {
    @PrimaryKey
    open var id: String? = null
    var year: Int? = null
    var month: Int? = null
    var startTime: Date? = null
    var finishTime: Date? = null
    var ssid: String? = null

    fun WorkData() {
    }

    fun WorkData(
        year: Int,
        month: Int,
        startTime: Date,
        finishTime: Date,
        ssid: String
    ) {
        this.year = year
        this.month = month
        this.startTime = startTime
        this.finishTime = finishTime
        this.ssid = ssid
    }
}