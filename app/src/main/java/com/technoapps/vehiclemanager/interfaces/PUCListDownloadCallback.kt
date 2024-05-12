package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.PUCClass

interface PUCListDownloadCallback {
    fun setPUCDetailDownloadCallback(pucClassArrayList: ArrayList<PUCClass>)
}