package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.PermitClass

interface PermitListDownloadCallback {
    fun setPermitDetailDownloadCallback(permitClassArrayList: ArrayList<PermitClass>)
}