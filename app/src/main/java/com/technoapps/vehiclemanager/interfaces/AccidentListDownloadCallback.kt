package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.AccidentClass

interface AccidentListDownloadCallback {
    fun setAccidentDetailDownloadCallback(accidentClassArrayList: ArrayList<AccidentClass>)
}