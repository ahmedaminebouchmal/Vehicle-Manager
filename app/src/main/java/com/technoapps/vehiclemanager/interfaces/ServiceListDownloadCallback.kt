package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.ServiceClass
import java.util.ArrayList

interface ServiceListDownloadCallback {
    fun setServiceDetailDownloadCallback(serviceClassArrayList: ArrayList<ServiceClass>)
}