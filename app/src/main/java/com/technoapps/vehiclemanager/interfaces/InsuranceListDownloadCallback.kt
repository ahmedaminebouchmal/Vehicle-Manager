package com.technoapps.vehiclemanager.interfaces

import com.technoapps.vehiclemanager.pojo.InsuranceClass
import java.util.ArrayList

interface InsuranceListDownloadCallback {
    fun setInsuranceDetailDownloadCallback(insuranceClassArrayList: ArrayList<InsuranceClass>)
}