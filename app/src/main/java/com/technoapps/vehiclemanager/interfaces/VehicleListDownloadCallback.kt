package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.VehicleClass
import java.util.ArrayList

interface VehicleListDownloadCallback {
    fun setVehicleDetailDownloadCallback(vehicleClassArrayList: ArrayList<VehicleClass>)
}