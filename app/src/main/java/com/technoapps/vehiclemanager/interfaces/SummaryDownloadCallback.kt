package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.SummaryClass

interface SummaryDownloadCallback {
    fun setSummaryDetailDownloadCallback(summaryClassArrayList : ArrayList<SummaryClass>)
}