package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.RefuelClass
import java.util.ArrayList

interface RefuelListDownloadCallback {
    fun setRefuelDetailDownloadCallback(refuelClassArrayList: ArrayList<RefuelClass>)
}