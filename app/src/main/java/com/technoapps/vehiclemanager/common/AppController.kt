package com.technoapps.vehiclemanager.common

import android.app.Application

class AppController : Application() {

    companion object {
        var BaseUrl = ""
    }


    override fun onCreate() {
        super.onCreate()
        BaseUrl = "https://console.firebase.google.com/project/vehicle-manager-fd0f8/database/vehicle-manager-fd0f8-default-rtdb/data/~2F"
    }
}